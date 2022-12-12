package com.starters.yeogida.presentation.user

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.*
import com.starters.yeogida.data.remote.request.LoginRequestData
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.SignUpResponseData
import com.starters.yeogida.databinding.ActivityJoinBinding
import com.starters.yeogida.network.ApiClient
import com.starters.yeogida.util.ImageUtil
import com.starters.yeogida.util.UriUtil
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinBinding

    private val joinViewModel: JoinViewModel by viewModels()

    private val userService = ApiClient.userService
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private lateinit var userEmail: String
    private lateinit var userNum: String
    private var userNickname: String? = null
    private var userProfileImageUrl: String? = null

    private val PERMISSION_ALBUM = 101
    private val REQUEST_STORAGE = 1000
    private var permissionRejectCount = 0
    private var imageFile: File? = null

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data

            applicationContext?.let { context ->
                imageUri?.let { imageUri ->
                    imageFile = ImageUtil.getResizePicture(this, imageUri)
                    Log.e("imageFile", "Null ? ${imageFile == null}")
                }
            }

            imageUri?.let { imageUri ->
                GlideApp.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .into(binding.ivProfile)
            }
        }
    }

    // 뒤로가기 콜백
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 뒤로가기 클릭 시 실행시킬 코드 입력
            Log.e("JoinActivity/OnBackPressed", "뒤로가기 클릭")
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                } else {
                    shortToast("회원가입을 취소하셨습니다.")
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)
        binding.viewModel = joinViewModel
        binding.lifecycleOwner = this

        initIntent()
        setNicknameFilter()
        setTextChangedListener()
        setNickname()
        setProfileImage()
        setOnBackPressed()
        setSubmitButtonListener()
        setProfileImageListener()
    }

    private fun initIntent() {
        userEmail = intent.getStringExtra("email")?.let { it }.toString()
        userNum = intent.getStringExtra("userNum")?.let { it }.toString()
        userNickname = intent.getStringExtra("nickname")?.let { it }
        userProfileImageUrl = intent.getStringExtra("profileImageUrl")?.let { it }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.e("requestCode", requestCode.toString())
        when (requestCode) {
            REQUEST_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigatePhotos()
                } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                    if (permissionRejectCount == 0) {
                        permissionRejectCount++
                    }
                } else {

                }
            }

            PERMISSION_ALBUM -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigatePhotos()
                } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                    if (permissionRejectCount == 1) {
                        permissionRejectCount++
                    }
                }
            }

            else -> {

            }
        }
    }

    private fun setProfileImageListener() {
        joinViewModel.startGalleryEvent.observe(this) {
            Log.i("BUTTON", "initAdd")

            when {
                ContextCompat.checkSelfPermission(
                    this,
                    READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    navigatePhotos()
                    Log.i("BUTTON", "navigate")
                }

                shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup(this)
                    Log.i("BUTTON", "showpermission")
                }

                permissionRejectCount == 2 -> {
                    showSettingDialog(this)
                }

                else -> {
                    requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQUEST_STORAGE)
                    Log.i("BUTTON", "else")
                }
            }
        }
    }

    private fun showSettingDialog(context: Context) {
        AlertDialog.Builder(context).apply {
            setMessage("사진을 가져오려면 권한을 허용해주세요.")
            setPositiveButton("설정으로 이동") { _, _ ->
                Toast.makeText(
                    context,
                    "권한 허용을 위해 설정으로 이동합니다.",
                    Toast.LENGTH_SHORT
                ).show()
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                startActivity(intent)
            }
            create()
            show()
        }
    }

    private fun showPermissionContextPopup(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle("권한이 필요합니다.")
            setMessage("앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            setPositiveButton("동의") { _, _ ->
                requestPermissions(
                    this@JoinActivity,
                    arrayOf(READ_EXTERNAL_STORAGE),
                    PERMISSION_ALBUM
                )
            }
            setNegativeButton("취소") { _, _ -> }
            create()
            show()
        }
    }

    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageResult.launch(intent)
    }

    private fun setSubmitButtonListener() {
        binding.btnJoinSubmit.setOnClickListener {
            val nickname = binding.etNick.text.toString()

            val requestEmail = userEmail.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestUserNum = userNum.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestNickname = nickname.toRequestBody("text/plain".toMediaTypeOrNull())

            val partMap = hashMapOf<String, RequestBody>()

            partMap["email"] = requestEmail
            partMap["kakaoId"] = requestUserNum
            partMap["nickname"] = requestNickname

            val requestFile = imageFile?.asRequestBody("image/*".toMediaTypeOrNull())
                ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

            val partImg = MultipartBody.Part.createFormData(
                "imgUrl", imageFile?.name ?: "", requestFile
            )

            // TODO. API 통신 ProgressBar
            // 회원가입
            CoroutineScope(Dispatchers.IO).launch {
                val signUpResponse = userService.addUser(
                    partImg, partMap
                )
                signUp(signUpResponse)
            }
        }
    }

    private suspend fun signUp(
        signUpResponse: retrofit2.Response<BaseResponse<SignUpResponseData>>
    ) {
        Log.e("SignUpResponseCode", signUpResponse.code().toString())
        when (signUpResponse.code()) {
            201 -> {
                // 로그인 실행
                val loginResponse = userService.postLogin(
                    LoginRequestData(
                        userEmail, userNum
                    )
                )

                Log.d("loginResponse", "$loginResponse")

                when (loginResponse.code()) {
                    200 -> {
                        val accessToken = loginResponse.body()?.data?.accessToken
                        val refreshToken = loginResponse.body()?.data?.refreshToken

                        dataStore.saveIsLogin(true)
                        dataStore.saveUserToken(accessToken, refreshToken)

                        withContext(Dispatchers.Main) {
                            Log.e(
                                "SignUp/userAccessToken", dataStore.userAccessToken.first()
                            )
                            Log.e(
                                "SignUp/userRefreshToken", dataStore.userRefreshToken.first()
                            )
                            startMain()
                        }
                    }
                    else -> {
                        Log.e("loginResponse/Error", loginResponse.message())
                    }
                }
            }
            400 -> {
                Log.d("Join/signUpResponseCode", "400")

                // 닉네임 중복
                if (signUpResponse.message().toString() == "AlreadyExistsNickname Error!") {
                    withContext(Dispatchers.Main) {
                        with(binding) {
                            tvWarnNickDescription.visibility = View.VISIBLE  // 중복된다는 경고 문구 보이기.
                            etNick.setBackgroundResource(R.drawable.rectangle_border_red_10)
                        }
                    }
                } else {
                    Log.e("signUpResponse", "$signUpResponse")
                }
            }
            else -> {
                Log.e("signUpResponse", "회원가입 실패")
            }
        }
    }

    private fun startMain() {
        Intent(this, MainActivity::class.java).also {
            it.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(it)
            shortToast("회원가입 되었습니다.")
        }
    }

    // 뒤로가기 -> LoginActivity (동의항목 다시 받기)
    private fun setOnBackPressed() {
        // 툴바 뒤로가기 Navigation
        binding.tbJoin.setNavigationOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                } else {
                    shortToast("회원가입을 취소하셨습니다.")
                    finish()
                }
            }
        }

        // Android 시스템 뒤로가기를 하였을 때, 콜백 설정
        onBackPressedDispatcher.addCallback(this@JoinActivity, onBackPressedCallback)
    }

    private fun setTextChangedListener() {
        binding.etNick.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 완료 버튼 Enabled 이벤트
                binding.btnJoinSubmit.isEnabled = !binding.etNick.text.isNullOrBlank()
                binding.etNick.setBackgroundResource(R.drawable.selector_et_blue)
                binding.tvWarnNickDescription.visibility = View.GONE
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setNickname() {
        userNickname?.let {
            Log.d("JoinActivity", "닉네임 : $it")
            with(binding.etNick) {
                requestFocus()  // Focusing 하기
                setText(it)
                setSelection(length())  // 커서 끝으로
            }

            showKeyboard()  // 키보드도 올라오게
        }
    }

    private fun setProfileImage() {
        userProfileImageUrl?.let { // 프로필 이미지 URL
            Log.d("JoinActivity", "사진 url : $it")

            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = convertUrlToBitmap(it)  // imageURL -> Bitmap
                val profileImageUri =
                    UriUtil.bitmapToUri(this@JoinActivity, bitmap, userNum) // Bitmap -> Uri
                Log.e("profileImageURI", profileImageUri.toString())

                withContext(Dispatchers.Main) {
                    GlideApp.with(binding.ivProfile)
                        .load(bitmap)
                        .circleCrop()
                        .into(binding.ivProfile)
                }

                profileImageUri?.let {
                    imageFile = UriUtil.toFile(this@JoinActivity, profileImageUri)
                    contentResolver.delete(profileImageUri, null, null) // Uri에 해당되는 값 갤러리에서 제거.
                }
            }
        }
    }

    private fun convertUrlToBitmap(imageUrl: String): Bitmap? {
        var bitmap: Bitmap? = null
        val connection: HttpURLConnection?

        try {
            val url = URL(imageUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET" // request 방식 설정
                connectTimeout = 10000 // 10초의 타임아웃
                doOutput = true // OutPutStream으로 데이터를 넘겨주겠다고 설정
                doInput = true // InputStream으로 데이터를 읽겠다는 설정
                useCaches = true // 캐싱 여부
                connect()
            }

            val resCode = connection.responseCode // 연결 상태 확인

            if (resCode == HttpURLConnection.HTTP_OK) { // 200일때 bitmap으로 변경
                val input = connection.inputStream
                bitmap =
                    BitmapFactory.decodeStream(input) // BitmapFactory의 메소드를 통해 InputStream으로부터 Bitmap을 만들어 준다.
                connection.disconnect()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    private fun setNicknameFilter() {
        /** 문자열필터(EditText Filter) */
        var filterAlphaNumSpace = InputFilter { source, start, end, dest, dstart, dend ->
            /*
                [요약 설명]
                1. 정규식 패턴 ^[a-z] : 영어 소문자 허용
                2. 정규식 패턴 ^[A-Z] : 영어 대문자 허용
                3. 정규식 패턴 ^[ㄱ-ㅣ가-힣] : 한글 허용
                4. 정규식 패턴 ^[0-9] : 숫자 허용
                5. 정규식 패턴 ^[ ] or ^[\\s] : 공백 허용
            */
            val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9\\s]+$")
            if (!ps.matcher(source).matches()) {
                ""
            } else source
        }
        var lengthFilter = InputFilter.LengthFilter(8)

        binding.etNick.filters = arrayOf(filterAlphaNumSpace, lengthFilter)
    }

    private fun showKeyboard() {
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.ime())
    }
}