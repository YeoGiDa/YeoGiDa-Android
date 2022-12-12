package com.starters.yeogida.presentation.around

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.starters.yeogida.BuildConfig
import com.starters.yeogida.data.local.PlaceBottomSheetData
import com.starters.yeogida.databinding.FragmentAroundBinding

class AroundFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var binding: FragmentAroundBinding
    private lateinit var mView: MapView
    private lateinit var mMap: GoogleMap
    private lateinit var placeBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAroundBinding.inflate(inflater, container, false)

        mView = binding.mapViewAround
        mView.onCreate(savedInstanceState)
        mView.getMapAsync {
            val center = LatLng(36.38, 127.51)
            it.moveCamera(CameraUpdateFactory.newLatLng(center))
            it.moveCamera(CameraUpdateFactory.zoomTo(7f))
        }

        setPlaceBottomSheet()

        return binding.root
    }

    private fun startProcess() {
        mView.getMapAsync(this)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        updateLocation()
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener {
            placeBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 600000) // 위치 정보를 요청할 정확도와 주기 설정
            .setWaitForAccurateLocation(false)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                locationRequest.let {
                    for ((i, location) in p0.locations.withIndex()) {
                        setLastLocation(location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("MissingPermission")
    // 위치 정보를 받아서 마커를 그리고 화면 이동
    fun setLastLocation(lastLocation: Location) {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(15f)
            .build()
        mMap.clear()
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setPadding(20, 200, 20, 20)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        placeBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        placeBottomSheetBehavior.isDraggable = true
        initBottomSheet()
        initBottomSheetAdapter()
        return true
    }

    private fun initBottomSheet() {
        with(binding.bottomSheetPlace) {
            tvPlaceBottomSheetAddress.text = "서울시 종로구 땡땡길 123 1"
            tvPlaceBottomSheetName.text = "여기는 종로"
        }
    }

    private fun initBottomSheetAdapter() {
        val placeAdapter = PlaceBottomSheetAdapter()
        binding.bottomSheetPlace.rvPlaceBottomSheet.adapter = placeAdapter
        placeAdapter.placeList.addAll(
            listOf(
                PlaceBottomSheetData(
                    "https://cdn.pixabay.com/photo/2017/08/02/14/26/winter-landscape-2571788_1280.jpg",
                    "엘사",
                    4.0
                ),
                PlaceBottomSheetData(
                    "https://cdn.pixabay.com/photo/2017/11/07/20/43/christmas-tree-2928142_1280.jpg",
                    "산타할아버지",
                    3.5
                ),
                PlaceBottomSheetData(
                    "https://cdn.pixabay.com/photo/2017/07/28/00/57/christmas-2547356_1280.jpg",
                    "전구왕국",
                    1.5
                ),
                PlaceBottomSheetData(
                    "https://cdn.pixabay.com/photo/2015/02/25/07/39/church-648430_1280.jpg",
                    "겨울이좋아",
                    3.0
                ),
                PlaceBottomSheetData(
                    "https://cdn.pixabay.com/photo/2014/04/10/15/37/snowman-321034_1280.jpg",
                    "눈사람괴담",
                    5.0
                )
            )
        )
        placeAdapter.notifyDataSetChanged()
    }

    private fun setPlaceBottomSheet() {
        placeBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomSheetPlace.layoutPlaceBottomSheet)
        placeBottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isDraggable = true
        }
    }

    // 권한 처리
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // 권한 요청 전에 검사하는 코드
    // 두 권한 중 하나라도 승인되어 있지 않으면 다시 요청
    private fun checkPermission() {
        var permittedAll = true
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(requireContext(), permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                permittedAll = false
                requestPermission()
                break
            }
        }
        if (permittedAll) {
            startProcess()
        }
    }

    private fun requestPermission() {
        activityResultLauncher.launch(permissions)
    }

    // 권한이 승인되지 않으면 한 번 더 확인
    private fun confirmAgain() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한 승인 확인")
            .setMessage("위치 관련 권한을 모두 승인하셔야 현재 위치 주변 장소를 확인할 수 있습니다. 권한 승인을 다시 하시겠습니까?")
            .setPositiveButton("네") { _, _ ->
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                startActivity(intent)
            }.setNegativeButton("아니요") { _, _ ->
            }.create()
            .show()
    }

    // 모두 다 승인이면 프로세스 시작, 아니면 confirmAgain 호출
    private val contract = ActivityResultContracts.RequestMultiplePermissions()

    private val activityResultLauncher = registerForActivityResult(contract) { resultMap ->
        val isAllGranted = permissions.all { e -> resultMap[e] == true }
        if (isAllGranted) {
            startProcess()
        } else {
            confirmAgain()
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
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
