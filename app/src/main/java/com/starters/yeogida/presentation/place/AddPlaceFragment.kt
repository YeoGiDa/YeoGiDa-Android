package com.starters.yeogida.presentation.place

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.chip.Chip
import com.starters.yeogida.BuildConfig
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentAddPlaceBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.common.CustomProgressDialog
import com.starters.yeogida.presentation.common.ImageActivity
import com.starters.yeogida.util.ImageUtil
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
import kotlin.properties.Delegates

class AddPlaceFragment : Fragment(), PlaceImageClickListener, OnMapReadyCallback {
    private lateinit var binding: FragmentAddPlaceBinding
    private val viewModel: AddPlaceViewModel by viewModels()

    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private val PERMISSION_ALBUM = 101
    private val REQUEST_STORAGE = 1000

    private val placeImageUriList = mutableListOf<Uri>()

    private var isTagSelected: Boolean = false

    private var tripId: Long = 0

    private var placeTitle: String? = null
    private var placeAddress: String? = null
    private var placeStar: Float? = null
    private var placeReviewContent: String? = null
    private var placeLongitude by Delegates.notNull<Double>()
    private var placeLatitude by Delegates.notNull<Double>()
    private var placeTag: String? = null
    private var placeImageFileList = mutableListOf<File?>()
    private lateinit var mMap: GoogleMap
    private lateinit var mView: MapView

    private lateinit var mContext: Context

    private lateinit var progressDialog: CustomProgressDialog

    private val placeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data

                data?.let { data ->
                    val place = Autocomplete.getPlaceFromIntent(data)

                    place.name?.let { name ->
                        Log.e("placeSelectionEvents/Name", name)

                        placeTitle = name
                        binding.tvAddPlaceName.text = name
                    }

                    place.address?.let { address ->
                        Log.e("placeSelectionEvents/address", address)

                        placeAddress = address
                    }

                    place.latLng?.let { latLng ->
                        Log.e("placeSelectionEvents/latLong", latLng.toString())

                        placeLatitude = latLng.latitude
                        placeLongitude = latLng.longitude

                        // 지도에 마커 찍기
                        binding.mapViewAddPlace.visibility = View.VISIBLE
                        val center = LatLng(placeLatitude, placeLongitude)
                        mView.getMapAsync {
                            it.moveCamera(CameraUpdateFactory.newLatLng(center))
                            it.moveCamera(CameraUpdateFactory.zoomTo(18f))
                        }
                        mMap.clear()
                        mMap.addMarker(MarkerOptions().position(center))
                    }

                    activeSubmitButton()
                }

                // Error status
                val status = Autocomplete.getStatusFromIntent(data)

                status?.let { status ->
                    Log.e("PlaceSelectionEvents/status", status.statusMessage.toString())
                    activeSubmitButton()
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mView.getMapAsync {
            it.moveCamera(CameraUpdateFactory.newLatLng(LatLng(37.335887, 126.584063)))
            it.moveCamera(CameraUpdateFactory.zoomTo(18f))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_place, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.view = this

        mView = binding.mapViewAddPlace
        mView.onCreate(savedInstanceState)
        mView.onResume()
        mView.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = CustomProgressDialog(mContext)

        getTripId()
        setOnBackPressed() // 뒤로가기 리스너

        setPlaceImageAdapter() // 장소 사진 리스트 어댑터 연결
        onAddPhotoButtonClicked() // 장소 사진 추가 버튼 클릭
        setTextChangedListener() // 리뷰 TextChangedListener
        setPlaceRating() // 별점 ChangedListener
        setPlaceTag() // 장소 태그
        setPlaceSearchListener() // 장소 이름

        setOnSubmitButtonClicked() // 완료 버튼 클릭 시, 주소 값, 태그 값
    }

    private fun getTripId() {
        tripId = requireArguments().getLong("tripId")
    }

    private fun setPlaceSearchListener() {
        val apiKey = getString(R.string.google_map_app_key)
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity().application, apiKey)
        }

        binding.tvAddPlaceName.setOnClickListener {
            openPlaceSearch()
        }
    }

    private fun openPlaceSearch() {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.NAME
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            fields
        )
            .setCountry("KR")
            .build(requireContext())

        placeResultLauncher.launch(intent)
    }

    private fun setTextChangedListener() {
        binding.etAddPlaceReview.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 완료 버튼 Enabled 이벤트
                with(binding.etAddPlaceReview) {
                    val length = this.length()
                    if (length == 200) {
                        requireContext().shortToast("리뷰는 200자를 넘을 수 없습니다.")
                    } else {
                        binding.tvAddPlaceReviewTextCount.text = "$length / 200"
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
            btnAddPlaceSubmit.isEnabled =
                placeTitle != null &&
                isTagSelected && // 태그
                ratingbarAddPlace.rating >= 1F && // 별점
                !etAddPlaceReview.text.isNullOrBlank() // 리뷰
        }
    }

    private fun setOnSubmitButtonClicked() {
        // TODO. 완료 버튼 클릭 시 API 연결
        binding.btnAddPlaceSubmit.setOnClickListener {
            progressDialog.showDialog()

            placeReviewContent = binding.etAddPlaceReview.text.toString() // 리뷰 내용

            val requestTitle = placeTitle?.toRequestBody("text/plain".toMediaTypeOrNull())
                ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

            val requestAddress = placeAddress?.toRequestBody("text/plain".toMediaTypeOrNull())
                ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

            val requestStar = placeStar.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val requestContent = placeReviewContent?.toRequestBody("text/plain".toMediaTypeOrNull())
                ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

            val requestLongitude =
                placeLongitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val requestLatitude =
                placeLatitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val requestTag = placeTag?.toRequestBody("text/plain".toMediaTypeOrNull())
                ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

            val partMap = hashMapOf<String, RequestBody>()
            partMap["title"] = requestTitle
            partMap["address"] = requestAddress
            partMap["star"] = requestStar
            partMap["content"] = requestContent
            partMap["longitude"] = requestLongitude
            partMap["latitude"] = requestLatitude
            partMap["tag"] = requestTag

            // 이미지
            val requestPlaceImages = mutableListOf<MultipartBody.Part>()

            // placeImageUriList -> placeImageFileList
            placeImageFileList.clear()

            placeImageUriList.forEach { uri ->
                placeImageFileList.add(
                    ImageUtil.getResizePicture(requireContext(), uri)
                )
            }

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
                val requestImageFile = "".toRequestBody("text/plain".toMediaTypeOrNull())
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
                val response = placeService.postPlace(
                    dataStore.userBearerToken.first(),
                    tripId,
                    partMap,
                    requestPlaceImages
                )

                when (response.code()) {
                    201 -> {
                        withContext(Dispatchers.Main) {
                            findNavController().navigateUp()
                            findNavController().navigate(
                                R.id.action_aroundPlace_to_placeDetail,
                                bundleOf("placeId" to response.body()?.data?.placeId)
                            )
                            progressDialog.dismissDialog()
                        }
                    }

                    403 -> {
                        Log.e("AddPlace/Error", response.toString())
                        withContext(Dispatchers.Main) {
                            progressDialog.dismissDialog()
                        }
                    }

                    404 -> { // 여행지가 존재하지 않을 경우
                        Log.e("AddPlace/Error", response.toString())
                        withContext(Dispatchers.Main) {
                            progressDialog.dismissDialog()
                        }
                    }

                    else -> {
                        Log.e("AddPlace/Error", response.toString())
                        withContext(Dispatchers.Main) {
                            progressDialog.dismissDialog()
                        }
                    }
                }
            }
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
        binding.ratingbarAddPlace.onRatingBarChangeListener = (
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                if (rating < 1.0F) {
                    ratingBar?.rating = 1.0F
                } else {
                    with(binding.tvAddPlaceRatingTitle) {
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
        binding.rvAddPlacePhoto.adapter = AddPlaceImgAdapter(placeImageUriList, this)
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

    override fun deleteImage(imageUri: Uri) {
        CustomDialog(requireContext()).apply {
            showDialog()
            setTitle("사진을 삭제하시겠어요?")
            setPositiveBtn("확인") {
                placeImageUriList.remove(imageUri)
                binding.rvAddPlacePhoto.adapter?.notifyDataSetChanged()
                dismissDialog()
            }
            setNegativeBtn("닫기") {
                dismissDialog()
            }
        }
    }

    override fun openImageScreen(imageUri: Uri) {
        val intent = Intent(requireContext(), ImageActivity::class.java)
        intent.putExtra("imageUri", imageUri.toString())
        startActivity(intent)
    }

    private fun navigatePhotos() {
        TedImagePicker.with(requireContext())
            .buttonBackground(R.color.main_blue)
            .dropDownAlbum()
            .max(10, "최대 10장까지 가능합니다.")
            .selectedUri(placeImageUriList)
            .startMultiImage { newSelectedUriList ->
                placeImageUriList.clear()
                placeImageUriList.addAll(newSelectedUriList)
                binding.rvAddPlacePhoto.adapter?.notifyDataSetChanged()
            }
    }

    private fun setOnBackPressed() {
        binding.tbAddPlace.setNavigationOnClickListener {
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
            setTitle("작성을 취소하시겠습니까?")

            setPositiveBtn("확인") {
                dismissDialog()
                requireContext().shortToast("글 작성을 취소하였습니다")
                findNavController().navigateUp()
            }
            setNegativeBtn("닫기") {
                dismissDialog()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }

    override fun onDestroy() {
        mView.onDestroy()
        super.onDestroy()
    }
}
