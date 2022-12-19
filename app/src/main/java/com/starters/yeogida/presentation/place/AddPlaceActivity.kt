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
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.chip.Chip
import com.starters.yeogida.BuildConfig
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.ActivityAddPlaceBinding
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.common.ImageActivity
import com.starters.yeogida.util.shortToast
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddPlaceActivity : AppCompatActivity(), PlaceImageClickListener {
    private lateinit var binding: ActivityAddPlaceBinding
    private val viewModel: AddPlaceViewModel by viewModels()

    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private val PERMISSION_ALBUM = 101
    private val REQUEST_STORAGE = 1000

    private val placeImageUriList = mutableListOf<Uri>()

    private var isTagSelected: Boolean = false

    private var placeId: String? = null
    private var placeTitle: String? = null
    private var placeAddress: String? = null
    private var placeStar: Float? = null
    private var placeReviewContent: String? = null
    private var placeLongitude: Double? = null
    private var placeLatitude: Double? = null
    private var placeTag: String? = null
    private var placeImageList = mutableListOf<File?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_place)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setOnBackPressed() // 뒤로가기 리스너

        setPlaceImageAdapter()
        onAddPhotoButtonClicked() // 장소 사진 추가 버튼 클릭
        setTextChangedListener()    // 리뷰 TextChangedListener
        setPlaceRating()   // 별점 ChangedListener
        setPlaceTag()       // 장소 태그

        setOnSubmitButtonClicked()  // 완료 버튼 클릭 시, 주소 값, 태그 값
    }

    private fun setTextChangedListener() {
        binding.etAddPlaceReview.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 완료 버튼 Enabled 이벤트
                with(binding.etAddPlaceReview) {
                    val length = this.length()
                    if (length == 200) {
                        shortToast("리뷰는 200자를 넘을 수 없습니다.")
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
                placeTitle != null
                        && isTagSelected // 태그
                        && ratingbarAddPlace.rating >= 1F // 별점
                        && !etAddPlaceReview.text.isNullOrBlank() // 리뷰
        }
    }

    private fun setOnSubmitButtonClicked() {
        // TODO. 완료 버튼 클릭 시 API 연결

        placeReviewContent = binding.etAddPlaceReview.text.toString()   // 리뷰 내용
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
                })
    }

    private fun setPlaceImageAdapter() {
        binding.rvAddPlacePhoto.adapter = AddPlaceImgAdapter(placeImageUriList, this)
    }


    private fun onAddPhotoButtonClicked() {
        viewModel.openGalleryEvent.observe(this) {

            CoroutineScope(Dispatchers.IO).launch {
                val isRejected = dataStore.imagePermissionIsRejected.first()

                when {
                    ContextCompat.checkSelfPermission(
                        this@AddPlaceActivity,
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
                            showSettingDialog(this@AddPlaceActivity)
                        }
                    }

                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        withContext(Dispatchers.Main) {
                            showPermissionContextPopup(this@AddPlaceActivity)
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
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                } else {

                }
            }

            PERMISSION_ALBUM -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigatePhotos()
                } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
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
                    this@AddPlaceActivity,
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
        placeImageUriList.remove(imageUri)
        binding.rvAddPlacePhoto.adapter?.notifyDataSetChanged()
    }

    override fun openImageScreen(imageUri: Uri) {
        val intent = Intent(this, ImageActivity::class.java)
        intent.putExtra("imageUri", imageUri.toString())
        startActivity(intent)
    }

    private fun navigatePhotos() {
        TedImagePicker.with(this)
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
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun showCancelDialog() {
        CustomDialog(this).apply {
            showDialog()
            setTitle("작성을 취소하시겠습니까?")

            setPositiveBtn("확인") {
                dismissDialog()
                shortToast("글 작성을 취소하였습니다")
                finish()
            }
            setNegativeBtn("닫기") {
                dismissDialog()
            }
        }
    }
}