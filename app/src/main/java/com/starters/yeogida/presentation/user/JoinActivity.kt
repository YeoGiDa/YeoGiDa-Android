package com.starters.yeogida.presentation.user

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.GlideApp
import com.starters.yeogida.MainActivity
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.request.LoginRequestData
import com.starters.yeogida.data.remote.request.SignUpRequestData
import com.starters.yeogida.databinding.ActivityJoinBinding
import com.starters.yeogida.network.ApiClient
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinBinding
    private val dataStore = YeogidaApplication.getInstance().getDataStore()
    private val joinViewModel: JoinViewModel by viewModels()

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {

            val imageUri = result.data?.data
            applicationContext?.let { context ->
                if (imageUri != null) {
                    // val ImageFile = UriUtil.toFile(context, imageUri)
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

        setNicknameFilter()
        setTextChangedListener()
        setNickname()
        setProfileImage()
        setOnBackPressed()
        setSubmitButtonListener()

        joinViewModel.startGalleryEvent.observe(this) {
            Log.i("BUTTON", "initAdd")

            when {
                ContextCompat.checkSelfPermission(
                    this,
                    READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    navigatePhotos()
                }
                shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                    Log.i("BUTTON", "showpermission")
                }
                else -> {
                    requestPermissions(
                        arrayOf(READ_EXTERNAL_STORAGE),
                        1000
                    )
                    Log.i("BUTTON", "else")
                }
            }

        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    101
                )
            }
            .setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }

    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageResult.launch(intent)
        // startActivityForResult(intent, REQ_GALLERY)
    }

    private fun setSubmitButtonListener() {
        binding.btnJoinSubmit.setOnClickListener {
            val email = intent.getStringExtra("email")?.let { it }.toString()
            val userNum = intent.getStringExtra("userNum")?.let { it }.toString()
            val profileImageUrl = intent.getStringExtra("profileImageUrl")?.let { it }.toString()
            val nickname = binding.etNick.text.toString()

            val userService = ApiClient.userService

            // 회원가입
            CoroutineScope(Dispatchers.IO).launch {
                val signUpResponse = userService.addUser(
                    SignUpRequestData(
                        email,
                        userNum,
                        nickname,
                        profileImageUrl
                    )
                )
                Log.e("SignUpResponseCode", signUpResponse.code().toString())
                when (signUpResponse.code()) {
                    201 -> {
                        Log.d("Join/signUpResponseCode", "201")
                        // 로그인 실행
                        val loginResponse = userService.postLogin(
                            LoginRequestData(
                                email,
                                userNum
                            )
                        )

                        Log.d("loginResponse", "$loginResponse")

                        if (loginResponse.isSuccessful) {
                            Log.e("loginResponse", "$loginResponse")
                            dataStore.saveAccessToken(loginResponse.body()!!.data!!.accessToken)
                            dataStore.saveRefreshToken(loginResponse.body()!!.data!!.refreshToken)
                            dataStore.saveIsLogin(true)

                            withContext(Dispatchers.Main) {
                                Log.e("Join/userAccessToken", dataStore.userAccessToken.first())
                                Log.e("Join/userRefreshToken", dataStore.userRefreshToken.first())
                                startMain()
                            }
                        } else {
                            Log.e("loginResponse", "$loginResponse")
                        }
                    }
                    400 -> { // 닉네임 중복
                        Log.d("Join/signUpResponseCode", "400")
                        withContext(Dispatchers.Main) {
                            with(binding) {
                                tvWarnNickDescription.visibility = View.VISIBLE  // 중복된다는 경고 문구 보이기.
                                etNick.setBackgroundResource(R.drawable.rectangle_border_red_10)
                            }
                        }

                    }
                    else -> {
                        Log.e("signUpResponse", "회원가입 실패")
                    }
                }
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
        intent.getStringExtra("nickname")?.let {
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
        intent.getStringExtra("profileImageUrl")?.let { // 프로필 이미지 URL
            Log.d("JoinActivity", "사진 url : $it")
            GlideApp.with(this)
                .load(it)
                .circleCrop()
                .into(binding.ivProfile)
        }
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