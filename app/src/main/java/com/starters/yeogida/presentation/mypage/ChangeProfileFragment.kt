package com.starters.yeogida.presentation.mypage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.starters.yeogida.BuildConfig
import com.starters.yeogida.GlideApp
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentChangeProfileBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.util.ImageUtil
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
import java.io.FileOutputStream
import java.util.regex.Pattern

class ChangeProfileFragment : Fragment() {

    private lateinit var binding: FragmentChangeProfileBinding
    private val viewModel: MyPageViewModel by viewModels()

    private val myPageService = YeogidaClient.myPageService
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private var isProfilePhotoChanged = false       // 기존 프로필 사진에서 변경되었는지
    private var currentNickname = ""   // 기존 닉네임

    private var isChanged = false       // 둘 중 하나 변경되었는지

    private lateinit var originNickName: String
    private var userProfileImageUrl: String? = null

    private val PERMISSION_ALBUM = 101
    private val REQUEST_STORAGE = 1000

    private var imageFile: File? = null        // 전송할 이미지 파일

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageUri = result.data?.data

            isProfilePhotoChanged = true        // 프로필 사진이 기존에서 변경되었다.
            updateIsChanged()
            enableSubmitButton()

            context?.let { context ->
                imageUri?.let { imageUri ->
                    imageFile = ImageUtil.getResizePicture(requireContext(), imageUri)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        getOriginUserData()
        enableSubmitButton()
        setOnBackPressed()

        setNicknameChangedListener()
        setSubmitButtonListener()
        setOnGalleryClicked()
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
                    requireContext().shortToast("권한을 거부하였습니다.")
                } else {

                }
            }

            PERMISSION_ALBUM -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigatePhotos()
                } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    requireContext().shortToast("권한을 거부하였습니다.")
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveIsImgPermissionRejected(true)
                    }
                }
            }

            else -> {

            }
        }
    }

    private fun setOnGalleryClicked() {
        viewModel.openGalleryEvent.observe(viewLifecycleOwner) {
            CoroutineScope(Dispatchers.IO).launch {
                val isRejected = dataStore.imagePermissionIsRejected.first()

                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        withContext(Dispatchers.Main) {
                            navigatePhotos()
                            dataStore.saveIsImgPermissionRejected(false)
                        }
                        Log.i("BUTTON", "navigate")
                    }

                    isRejected -> {
                        withContext(Dispatchers.Main) {
                            showSettingDialog(requireContext())
                        }
                    }

                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        withContext(Dispatchers.Main) {
                            showPermissionContextPopup(requireContext())
                        }
                        Log.i("BUTTON", "showpermission")
                    }

                    else -> {
                        withContext(Dispatchers.Main) {
                            requestPermissions(
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                REQUEST_STORAGE
                            )
                        }
                        Log.i("BUTTON", "else")
                    }
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
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
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
        binding.btnSubmit.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val nickname = binding.etNick.text.toString()

                val requestNickname = nickname.toRequestBody("text/plain".toMediaTypeOrNull())

                val partMap = hashMapOf<String, RequestBody>()
                partMap["nickname"] = requestNickname

                Log.e("requestFile", "${imageFile == null}")

                val requestFile = imageFile?.asRequestBody("image/*".toMediaTypeOrNull())
                    ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

                val partImg = MultipartBody.Part.createFormData(
                    "imgUrl", imageFile?.name ?: "", requestFile
                )

                Log.e("partImg", partImg.toString())

                // TODO. API 통신 ProgressBar
                val response = myPageService.changeMyProfile(
                    dataStore.userBearerToken.first(),
                    partImg,
                    partMap
                )

                when (response.code()) {
                    200 -> {
                        withContext(Dispatchers.Main) {
                            requireContext().shortToast("정보가 수정되었습니다.")
                            findNavController().navigateUp()
                        }
                    }
                    400 -> {
                        withContext(Dispatchers.Main) {
                            with(binding) {
                                tvWarnNickDescription.visibility =
                                    View.VISIBLE  // 중복된다는 경고 문구 보이기.
                                etNick.setBackgroundResource(R.drawable.rectangle_border_red_10)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setOnBackPressed() {
        binding.tbChangeProfile.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateIsChanged() {
        isChanged = isProfilePhotoChanged || (originNickName != currentNickname)
    }

    private fun enableSubmitButton() {
        binding.btnSubmit.isEnabled = isChanged && isValidNickName(currentNickname)
    }

    private fun setNicknameChangedListener() {
        binding.etNick.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                currentNickname = binding.etNick.text.toString()

                updateIsChanged()
                enableSubmitButton()

                binding.etNick.setBackgroundResource(R.drawable.selector_et_blue)
                binding.tvWarnNickDescription.visibility = View.GONE
            }
        })
    }

    private fun isValidNickName(nickname: String): Boolean {
        val regex = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{2,8}\$"
        val pattern = Pattern.compile(regex)

        val matcher = pattern.matcher(nickname)

        return matcher.matches() && !nickname.isNullOrBlank()
    }

    private fun getOriginUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = myPageService.getMyProfile(
                dataStore.userBearerToken.first()
            )

            when (response.code()) {
                200 -> {
                    response.body()?.data?.let { data ->
                        originNickName = data.nickname // 기존 닉네임 받아오기
                        userProfileImageUrl = data.imgUrl   // 기존 프로필 이미지 url

                        Log.e("imgUrl", userProfileImageUrl.toString())
                        initImageFile()     // 기존 프로필 사진 imageFile init

                        // 기존 정보로 EditText, ImageView 채우기
                        withContext(Dispatchers.Main) {
                            binding.currentUser = data
                            binding.executePendingBindings()
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun initImageFile() {
        userProfileImageUrl?.let { // 프로필 이미지 URL
            Log.d("initImageFile", "사진 url : $it")

            GlideApp.with(requireContext())
                .asBitmap()
                .load(it)
                .into(object : CustomTarget<Bitmap>(1920, 1080) {
                    override fun onResourceReady(
                        bitmap: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        saveImage(bitmap)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    internal fun saveImage(image: Bitmap) {
        val savedImagePath: String

        val imageFileName = System.currentTimeMillis().toString() + ".jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ).toString() + "/Folder Name"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val saveFile = File(storageDir, imageFileName)
            savedImagePath = saveFile.absolutePath
            try {
                val fOut = FileOutputStream(saveFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            imageFile = File(savedImagePath)
        }
    }
}