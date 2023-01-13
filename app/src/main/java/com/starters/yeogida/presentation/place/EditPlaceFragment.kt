package com.starters.yeogida.presentation.place

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.starters.yeogida.BuildConfig
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.response.place.PlaceImg
import com.starters.yeogida.databinding.FragmentEditPlaceBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.common.CustomProgressDialog
import com.starters.yeogida.presentation.common.ImageActivity
import com.starters.yeogida.util.ImageUtil
import com.starters.yeogida.util.UriUtil
import com.starters.yeogida.util.shortToast
import gun0912.tedimagepicker.builder.TedImagePicker
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
import java.net.URL

class EditPlaceFragment : Fragment(), PlaceEditImageClickListener {

    private lateinit var binding: FragmentEditPlaceBinding
    private val viewModel: AddPlaceViewModel by viewModels()

    private var placeId: Long = 0

    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private val PERMISSION_ALBUM = 101
    private val REQUEST_STORAGE = 1000

    private var isTagSelected: Boolean = false

    private var placeStar: Float? = null
    private var placeReviewContent: String? = null

    private var placeTag: String? = null

    private val placeImageFileList = mutableListOf<File?>()    // 기존 이미지 파일

    private lateinit var mContext: Context

    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        placeId = requireArguments().getLong("placeId")
        progressDialog = CustomProgressDialog(mContext)

        setOnBackPressed() // 뒤로가기 리스너
        getPlaceDetail()

        onAddPhotoButtonClicked() // 장소 사진 추가 버튼 클릭
        setTextChangedListener() // 리뷰 TextChangedListener
        setPlaceRating() // 별점 ChangedListener
        setPlaceTag() // 장소 태그

        setOnSubmitButtonClicked()
    }

    private fun setOnSubmitButtonClicked() {
        binding.btnEditPlaceSubmit.setOnClickListener {
            progressDialog.showDialog()

            placeReviewContent = binding.etEditPlaceReview.text.toString() // 리뷰 내용

            fun String.toPlainRequestBody() = this.toRequestBody("text/plain".toMediaTypeOrNull())

            val requestStar = placeStar.toString().toPlainRequestBody()

            val requestContent = placeReviewContent?.toPlainRequestBody()
                ?: "".toPlainRequestBody()

            val requestTag = placeTag?.toPlainRequestBody()
                ?: "".toPlainRequestBody()

            val partMap = hashMapOf<String, RequestBody>()

            partMap["content"] = requestContent
            partMap["star"] = requestStar
            partMap["tag"] = requestTag

            // 이미지
            val requestPlaceImages = mutableListOf<MultipartBody.Part>()

            if (placeImageFileList.isNotEmpty()) {
                placeImageFileList.forEach { imageFile ->
                    imageFile?.let {
                        val requestImageFile =
                            imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                        val partImg = MultipartBody.Part.createFormData(
                            "imgUrls",
                            imageFile.name,
                            requestImageFile
                        )
                        requestPlaceImages.add(partImg)
                    }
                }
            } else {
                val requestImageFile = "".toPlainRequestBody()
                requestPlaceImages.add(
                    MultipartBody.Part.createFormData(
                        "imgUrls",
                        "",
                        requestImageFile
                    )
                )
            }

            val placeService = YeogidaClient.placeService
            CoroutineScope(Dispatchers.IO).launch {
                Log.e("EditPlace", dataStore.userBearerToken.first())
                val response = placeService.editPlace(
                    placeId,
                    partMap,
                    requestPlaceImages
                )

                when (response.code()) {
                    200 -> {
                        withContext(Dispatchers.Main) {
                            findNavController().navigateUp()
                            progressDialog.dismissDialog()
                        }
                    }

                    403 -> {
                        Log.e("EditPlace/Error", response.toString())
                        withContext(Dispatchers.Main) {
                            progressDialog.dismissDialog()
                        }
                    }

                    else -> {
                        Log.e("EditPlace/Error", response.toString())
                        withContext(Dispatchers.Main) {
                            progressDialog.dismissDialog()
                        }
                    }
                }
            }
        }
    }

    private fun getPlaceDetail() {
        progressDialog.showDialog()
        val placeService = YeogidaClient.placeService

        CoroutineScope(Dispatchers.IO).launch {
            val response = placeService.getPlaceDetail(placeId)

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let {
                        withContext(Dispatchers.Main) {
                            binding.place = data
                            binding.executePendingBindings()

                            // 기존 장소 태그 선택되게
                            for (i in 0 until binding.chipGroup.childCount) {
                                val currChip = binding.chipGroup.getChildAt(i) as Chip
                                if (
                                    currChip.text == data.tag
                                ) {
                                    currChip.isChecked = true
                                }
                            }
                        }
                        // 기존 장소 사진 목록 가져오기
                        if (data.placeImgs[0].imgUrl != "https://yeogida-bucket.s3.ap-northeast-2.amazonaws.com/default_place.png") {
                            // 기본 이미지가 아닐 때
                            initImageFiles(data.placeImgs)
                        } else {
                            withContext(Dispatchers.Main) {
                                progressDialog.dismissDialog()
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun initImageFiles(placeImgs: List<PlaceImg>) {
        CoroutineScope(Dispatchers.IO).launch {
            placeImgs.forEach { placeImg ->
                val bitmap = BitmapFactory.decodeStream(URL(placeImg.imgUrl).openStream())
                bitmapToImageFile(bitmap)
            }
        }.join()

        withContext(Dispatchers.Main) {
            setPlaceImageAdapter()
            binding.rvEditPlacePhoto.adapter?.notifyItemRangeInserted(0, placeImgs.size)
            progressDialog.dismissDialog()
        }
    }

    private fun bitmapToImageFile(bitmap: Bitmap?) {
        val uri = UriUtil.bitmapToCompressedUri(mContext, bitmap, "")
        uri?.let {
            placeImageFileList.add(
                ImageUtil.getResizePicture(
                    mContext,
                    uri
                )
            )     // 기존 이미지 파일들 이미지 파일 리스트에 추가
            mContext.contentResolver.delete(uri, null, null) // Uri에 해당되는 값 갤러리에서 제거.
        }
    }

    private fun setTextChangedListener() {
        binding.etEditPlaceReview.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 완료 버튼 Enabled 이벤트
                with(binding.etEditPlaceReview) {
                    val length = this.length()
                    if (length == 200) {
                        requireContext().shortToast("리뷰는 200자를 넘을 수 없습니다.")
                    } else {
                        binding.tvEditPlaceReviewTextCount.text = "$length / 200"
                        activeSubmitButton()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun activeSubmitButton() {
        with(binding) {
            btnEditPlaceSubmit.isEnabled =
                isTagSelected && // 태그
                        ratingbarEditPlace.rating >= 1F && // 별점
                        !etEditPlaceReview.text.isNullOrBlank() // 리뷰
        }
    }

    private fun setPlaceTag() {
        with(binding.chipGroup) {
            setOnCheckedStateChangeListener { group, checkedIds ->
                if (checkedIds.isEmpty()) {
                    isTagSelected = false
                } else {
                    isTagSelected = true
                    val selectedTag =
                        findViewById<Chip>(binding.chipGroup.checkedChipId).text.toString()
                    placeTag = selectedTag
                }
                activeSubmitButton()
            }
        }
    }

    private fun setPlaceRating() {
        binding.ratingbarEditPlace.onRatingBarChangeListener = (
                RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    if (rating < 1.0F) {
                        ratingBar?.rating = 1.0F
                    } else {
                        with(binding.tvEditPlaceRatingTitle) {
                            text = when (rating) {
                                1.0F, 1.5F -> "매우 별로"
                                2.0F, 2.5F -> "별로"
                                3.0F, 3.5F -> "보통"
                                4.0F, 4.5F -> "좋아요"
                                else -> "매우 좋아요"
                            }
                        }
                    }
                    placeStar = rating
                    activeSubmitButton()
                }
                )
    }

    private fun setPlaceImageAdapter() {
        binding.rvEditPlacePhoto.adapter = EditPlaceImgAdapter(placeImageFileList, this)
    }

    private fun onAddPhotoButtonClicked() {
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
                    Log.e("허용", requestCode.toString())
                    navigatePhotos()
                } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Log.e("거부", requestCode.toString())
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

    override fun deleteImage(imageFile: File) {
        CustomDialog(requireContext()).apply {
            showDialog()
            setTitle("사진을 삭제하시겠어요?")
            setPositiveBtn("확인") {
                placeImageFileList.remove(imageFile)
                binding.rvEditPlacePhoto.adapter?.notifyDataSetChanged()
                dismissDialog()
            }
            setNegativeBtn("닫기") {
                dismissDialog()
            }
        }
    }

    override fun openImageScreen(imageFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val intent = Intent(mContext, ImageActivity::class.java)
            intent.putExtra("filePath", imageFile.absolutePath)
            startActivity(intent)
        }
    }

    private fun navigatePhotos() {
        TedImagePicker.with(requireContext())
            .buttonBackground(R.color.main_blue)
            .dropDownAlbum()
            .max(10 - placeImageFileList.size, "최대 10장까지 가능합니다.")
            .startMultiImage { newSelectedUriList ->
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        progressDialog.showDialog()
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        for (uri in newSelectedUriList) {
                            uri?.let {
                                val imageFile = ImageUtil.getResizePicture(mContext, uri)
                                placeImageFileList.add(imageFile)
                            }
                        }
                    }.join()
                    withContext(Dispatchers.Main) {
                        binding.rvEditPlacePhoto.adapter?.notifyDataSetChanged()
                        progressDialog.dismissDialog()
                    }
                }
            }
    }

    private fun setOnBackPressed() {
        binding.tbEditPlace.setNavigationOnClickListener {
            showCancelDialog()
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로가기 클릭 시 실행시킬 코드 입력
                showCancelDialog()
            }
        }

        // Android 시스템 뒤로가기를 하였을 때, 콜백 설정
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun showCancelDialog() {
        CustomDialog(requireContext()).apply {
            showDialog()
            setTitle("수정을 취소하시겠습니까?")

            setPositiveBtn("확인") {
                dismissDialog()
                requireContext().shortToast("수정을 취소하였습니다")
                findNavController().navigateUp()
            }
            setNegativeBtn("닫기") {
                dismissDialog()
            }
        }
    }
}