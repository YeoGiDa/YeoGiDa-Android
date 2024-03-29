package com.starters.yeogida.presentation.trip

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.starters.yeogida.BuildConfig
import com.starters.yeogida.GlideApp
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentAddTripBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomProgressDialog
import com.starters.yeogida.presentation.common.ImageActivity
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.util.ImageUtil
import com.starters.yeogida.util.UriUtil
import com.starters.yeogida.util.customEnqueue
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

class AddTripFragment : Fragment() {
    private lateinit var binding: FragmentAddTripBinding
    private var imageFile: File? = null

    private lateinit var mContext: Context
    private lateinit var progressDialog: CustomProgressDialog

    // 수정할 때 필요한 TripId
    private var tripId: Long = 0

    private var type: String? = null
    private var isChanged = false

    private var selectedPicUri: Uri? = null
    private var originImgUrl: String? = null

    private val PERMISSION_ALBUM = 101
    private val REQUEST_STORAGE = 1000
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (type == "edit") {
                    isChanged = true
                }
                result.data?.let {
                    selectedPicUri = it.data!!
                    originImgUrl = null
                    with(binding) {
                        setPhotoVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE)
                        Glide.with(requireContext()).load(selectedPicUri).into(ivAddTripPhoto)
                    }
                }

                activity?.applicationContext?.let { _ ->
                    selectedPicUri?.let { imageUri ->
                        imageFile = ImageUtil.getResizePicture(requireContext(), imageUri)
                        Log.e("imageFile", "Null ? ${imageFile == null}")
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_trip, container, false)
        binding.view = this

        progressDialog = CustomProgressDialog(mContext)

        setEditForm()
        initBottomSheet()
        initEditText()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setEditForm() {
        requireActivity().intent?.extras?.let { bundle ->
            with(bundle) {
                type = getString("type")
                type?.let {
                    if (type == "edit") {
                        getLong("tripId")?.let {
                            tripId = it
                        }
                        val region = getString("region")
                        val title = getString("title")
                        val subTitle = getString("subTitle")
                        val imgUrl = getString("imgUrl")

                        with(binding) {
                            tvAddTripTitle.text = "여행지 수정"
                            tvAddTripRegion.text = region // 지역
                            etAddTripName.setText(title) // 제목
                            etAddTripSubtitle.setText(subTitle) // 소제목

                            imgUrl?.let {
                                setPhotoVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE)

                                GlideApp.with(mContext) // 기존 여행지 대표사진
                                    .load(imgUrl)
                                    .into(ivAddTripPhoto)

                                originImgUrl = it

                                initImageFile(imgUrl)
                                activeConfirmButton()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initImageFile(imgUrl: String) {
        imgUrl?.let { // 프로필 이미지 URL
            Log.d("initImageFile", "사진 url : $it")

            GlideApp.with(requireContext())
                .asBitmap()
                .load(it)
                .into(object : CustomTarget<Bitmap>(1920, 1080) {
                    override fun onResourceReady(
                        bitmap: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmapToImageFile(bitmap)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    private fun bitmapToImageFile(bitmap: Bitmap) {
        val uri = UriUtil.bitmapToCompressedUri(mContext, bitmap, "")
        uri?.let {
            imageFile = UriUtil.toFile(mContext, uri)
            mContext.contentResolver.delete(uri, null, null) // Uri에 해당되는 값 갤러리에서 제거.
        }
    }

    private fun initBottomSheet() {
        binding.tvAddTripRegion.setOnClickListener {
            // binding.tvAddTripRegion.setBackgroundResource(R.drawable.rectangle_border_main_blue_10)
            val bottomSheetDialog = RegionBottomSheetFragment {
                binding.tvAddTripRegion.text = it
                if (type == "edit") {
                    isChanged = true
                }
                activeConfirmButton()
            }
            bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        }
    }

    // edit text에 사용되는 함수 연결
    private fun initEditText() {
        with(binding) {
            checkActiveAndLength(etAddTripSubtitle)
            checkActiveAndLength(etAddTripName)
        }
    }

    // 완료 버튼 활성화 및 최대 글자수 확인
    private fun checkActiveAndLength(et: EditText) {
        et.addTextChangedListener {
            if (type == "edit") {
                isChanged = true
            }
            activeConfirmButton()
            if (et.text.length == 10) {
                requireContext().shortToast("최대 10글자까지 작성 가능합니다.")
            }
        }
    }

    private fun activeConfirmButton() {
        with(binding) {
            if (type == "edit") {
                btnAddTripNext.isEnabled =
                    !tvAddTripRegion.text.isNullOrEmpty() &&
                    !etAddTripName.text.isNullOrEmpty() &&
                    !etAddTripSubtitle.text.isNullOrEmpty() &&
                    ivAddTripPhotoX.visibility == View.VISIBLE &&
                    isChanged
            } else {
                btnAddTripNext.isEnabled =
                    !tvAddTripRegion.text.isNullOrEmpty() &&
                    !etAddTripName.text.isNullOrEmpty() &&
                    !etAddTripSubtitle.text.isNullOrEmpty() &&
                    ivAddTripPhotoX.visibility == View.VISIBLE
            }
        }
    }

    // 이미지 확대 관련 visibility
    private fun setImageVisibility(bigVisibility: Int, btnVisibility: Int) {
        with(binding) {
            ivAddTripPhotoBig.visibility = bigVisibility
            btnAddTripNext.visibility = btnVisibility
        }
    }

    // 사진 추가, 삭제 관련 visibility
    private fun setPhotoVisibility(v1: Int, v2: Int, v3: Int) {
        with(binding) {
            ivAddTripPhoto.visibility = v1
            ivAddTripPhotoX.visibility = v2
            tvAddTripPhotoDescription.visibility = v3
        }
        activeConfirmButton()
    }

    private fun moveToAroundPlace(tripId: Long) {
        val intent = Intent(requireContext(), PlaceActivity::class.java)
        intent.putExtra("tripId", tripId)
        startActivity(intent)
        requireActivity().finish()
    }

    // 여행지 추가 api 연결
    fun postTrip(view: View) {
        progressDialog.showDialog()

        fun String?.toPlainRequestBody() =
            requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())

        val textHashMap = hashMapOf<String, RequestBody>()

        with(binding) {
            val regionRequestBody = tvAddTripRegion.text.toString().toPlainRequestBody()
            val titleRequestBody = etAddTripName.text.toString().toPlainRequestBody()
            val subTitleRequestBody = etAddTripSubtitle.text.toString().toPlainRequestBody()

            textHashMap["region"] = regionRequestBody
            textHashMap["title"] = titleRequestBody
            textHashMap["subTitle"] = subTitleRequestBody
        }

        val requestFile = imageFile?.asRequestBody("image/*".toMediaTypeOrNull())
            ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

        val tripImg = MultipartBody.Part.createFormData(
            "imgUrl", imageFile?.name ?: "", requestFile
        )

        if (type == "edit") {
            CoroutineScope(Dispatchers.IO).launch {
                val response = YeogidaClient.tripService.editTrip(
                    tripId,
                    textHashMap,
                    tripImg
                )

                when (response.code()) {
                    200 -> {
                        withContext(Dispatchers.Main) {
                            requireActivity().finish()
                            requireActivity().shortToast("수정이 완료되었습니다.")
                        }
                    }
                    400 -> {
                        withContext(Dispatchers.Main) {
                            requireActivity().shortToast("대표 이미지가 없습니다.")
                        }
                    }
                    403 -> {

                    }
                    else -> {
                        Log.e("EditTrip", "${response.code()}")
                    }
                }

                withContext(Dispatchers.Main) {
                    progressDialog.dismissDialog()
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                YeogidaClient.tripService.postTrip(
                    tripImg,
                    textHashMap
                ).customEnqueue(
                    onSuccess = { responseData ->
                        if (responseData.code == 201) {
                            responseData.data?.tripId?.let { tripId -> moveToAroundPlace(tripId) }
                            progressDialog.dismissDialog()
                        }
                    }, onFail = {
                    progressDialog.dismissDialog()
                }
                )
            }
        }
    }

    fun setOnGalleryClicked(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            val isRejected = dataStore.imagePermissionIsRejected.first()

            when {
                ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    withContext(Dispatchers.Main) {
                        connectGallery()
                        dataStore.saveIsImgPermissionRejected(false)
                    }
                }

                isRejected -> {
                    withContext(Dispatchers.Main) {
                        showSettingDialog(mContext)
                    }
                }

                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    withContext(Dispatchers.Main) {
                        showPermissionContextPopup(mContext)
                    }
                }

                else -> {
                    withContext(Dispatchers.Main) {
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_STORAGE
                        )
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

    // 갤러리 창 연결
    private fun connectGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        imageResult.launch(intent)
    }

    // 사진 전체 화면으로 보기
    fun setFullScreenImage(view: View) {
        val intent = Intent(activity, ImageActivity::class.java)
        if (selectedPicUri == null) {
            intent.putExtra("imgUrl", originImgUrl.toString())
        } else {
            intent.putExtra("imageUri", selectedPicUri.toString())
        }
        startActivity(intent)
    }

    fun setOriginalImage(view: View) {
        setImageVisibility(View.GONE, View.VISIBLE)
    }

    fun deletePhoto(view: View) {
        setPhotoVisibility(View.GONE, View.GONE, View.GONE)
    }

    fun close(view: View) {
        (activity as AddTripActivity).finish()
    }

    fun showDialog(view: View) {
        val myLayout = layoutInflater.inflate(R.layout.dialog_help, null)
        val build = android.app.AlertDialog.Builder(view.context).apply {
            setView(myLayout)
        }
        val dialog = build.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}
