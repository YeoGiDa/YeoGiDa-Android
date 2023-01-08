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
import com.google.maps.android.clustering.ClusterManager
import com.starters.yeogida.BuildConfig
import com.starters.yeogida.databinding.FragmentAroundBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.util.customEnqueue

class AroundFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var binding: FragmentAroundBinding
    private lateinit var mView: MapView
    private lateinit var mMap: GoogleMap
    private lateinit var placeBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var userList: ArrayList<Place> = ArrayList()

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
        getPlaceItem()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        updateLocation()
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener {
            placeBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun getPlaceItem() {
        var arrayList: ArrayList<Place> = ArrayList()

        YeogidaClient.aroundService.getClusterList().customEnqueue(
            onSuccess = {
                if (it.code == 200) {
                    // 위도 경도 리스트 만들기
                    for (i in 0 until (it.data?.placeList?.size ?: 0)) {
                        val mLatLng = it.data?.placeList?.get(i)?.let { place -> LatLng(place.latitude, place.longitude) }
                        if (mLatLng != null) {
                            val place = Place(it.data.placeList[i].placeId, mLatLng, it.data.placeList[i].title, it.data.placeList[i].address)
                            arrayList.add(place)
                        }
                    }
                }

                mView.getMapAsync {
                    userList = arrayList
                    setUpClusterManager(mMap, userList)
                }
            }
        )
    }

    // 커스텀 마커를 위해 남겨둠
//    private fun getAllItem(): ArrayList<Place> {
//        var arrayList: ArrayList<Place> = ArrayList()
//        val latLng1 = LatLng(37.570223492195, 126.98361037914)
//        val latLng2 = LatLng(37.570211191115, 126.98000031114)
//        val latLng3 = LatLng(37.570239992195, 126.98234031114)
//        val latLng4 = LatLng(37.57056992395, 126.981241037074)
//
//        val user1 = Place(1, latLng1)
//        val user2 = Place(2, latLng2)
//        val user3 = Place(3, latLng3)
//        val user4 = Place(4, latLng4)
//
//        arrayList.add(user1)
//        arrayList.add(user2)
//        arrayList.add(user3)
//        arrayList.add(user4)
//
//        return arrayList
//    }

    private fun setUpClusterManager(mMap: GoogleMap, list: ArrayList<Place>) {
        val clusterManager = ClusterManager<Place> (requireContext(), mMap)
        mMap.setOnCameraIdleListener(clusterManager)
        clusterManager.addItems(list)
        clusterManager.cluster()

        clusterManager.setOnClusterItemClickListener {
            placeBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            placeBottomSheetBehavior.isDraggable = true
            initBottomSheet(it.name, it.address)
            initBottomSheetAdapter(it.latLng.latitude, it.latLng.longitude)

            return@setOnClusterItemClickListener false
        }
    }

    // 현재 위치
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
        return true
    }

    private fun initBottomSheet(title: String, address: String) {
        with(binding.bottomSheetPlace) {
            tvPlaceBottomSheetAddress.text = address
            tvPlaceBottomSheetName.text = title
        }
    }

    private fun initBottomSheetAdapter(latitude: Double, longitude: Double) {
        val placeAdapter = PlaceBottomSheetAdapter() { tripId: Long, placeId: Long ->
            moveToDetail(tripId, placeId)
        }
        binding.bottomSheetPlace.rvPlaceBottomSheet.adapter = placeAdapter
        YeogidaClient.aroundService.getClusterMarkerData(
            latitude,
            longitude
        ).customEnqueue(
            onSuccess = {
                if (it.code == 200) {
                    it.data?.let { data -> placeAdapter.placeList.addAll(data.placeList) }
                    placeAdapter.notifyDataSetChanged()
                }
            }
        )
    }

    private fun setPlaceBottomSheet() {
        placeBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomSheetPlace.layoutPlaceBottomSheet)
        placeBottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isDraggable = true
        }
    }

    private fun moveToDetail(tripId: Long, placeId: Long) {
        val intent = Intent(requireContext(), PlaceActivity::class.java)
        intent.putExtra("type", "around")
        intent.putExtra("tripId", tripId)
        intent.putExtra("placeId", placeId)
        startActivity(intent)
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
        placeBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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
