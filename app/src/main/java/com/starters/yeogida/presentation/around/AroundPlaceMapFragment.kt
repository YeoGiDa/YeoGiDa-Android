package com.starters.yeogida.presentation.around

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.starters.yeogida.R
import com.starters.yeogida.data.remote.response.place.PlaceMapList
import com.starters.yeogida.databinding.FragmentAroundPlaceMapBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.util.customEnqueue

class AroundPlaceMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mView: MapView
    private lateinit var binding: FragmentAroundPlaceMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mPlaceMapList: List<PlaceMapList>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_around_place_map, container, false)
        binding.view = this

        mView = binding.mapViewAroundPlace
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)

        getPlaceList()
        initNavigation()

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val position = p0.id.substring(1).toInt()
        val bottomSheetDialog = PlaceMapBottomSheetFragment(mPlaceMapList[position])
        bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        return true
    }

    private fun getPlaceList() {
        YeogidaClient.placeService.getPlaceMapList(
            requireArguments().getLong("tripId")
        ).customEnqueue(
            onSuccess = {
                if (it.code == 200) {
                    // 장소들의 중심 좌표로 카메라 이동
                    it.data?.let { data -> initMapCamera(data.meanLat, data.meanLng) }
                    mPlaceMapList = it.data?.placeList ?: arrayListOf()

                    // 위도 경도 리스트 만들기
                    val locationArrayList = arrayListOf<LatLng>()
                    for (i in 0 until (it.data?.placeList?.size ?: 0)) {
                        val mLatLng = it.data?.placeList?.get(i)?.let { place -> LatLng(place.latitude, place.longitude) }
                        if (mLatLng != null) {
                            locationArrayList.add(mLatLng)
                        }
                    }
                    // 위도 경도 리스트로 마커 생성
                    createMarker(locationArrayList)
                }
            }
        )
    }

    // 장소들의 중심 좌표로 카메라 이동
    private fun initMapCamera(latitude: Double, longitude: Double) {
        val mLatLng = LatLng(latitude, longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
    }

    private fun initNavigation() {
        binding.tbAroundPlaceMap.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun createMarker(arrayList: ArrayList<LatLng>) {
        for (i in 0 until arrayList.size) {
            mMap.addMarker(MarkerOptions().position(arrayList[i]))
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
