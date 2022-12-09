package com.starters.yeogida.presentation.around

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.starters.yeogida.data.local.PlaceBottomSheetData
import com.starters.yeogida.databinding.FragmentAroundBinding

class AroundFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mView: MapView
    private lateinit var binding: FragmentAroundBinding
    private lateinit var mMap: GoogleMap
    private lateinit var placeBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAroundBinding.inflate(inflater, container, false)

        mView = binding.mapViewAround
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)
        setPlaceBottomSheet()

        return binding.root
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener {
            placeBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        createMarker()
    }

    private fun createMarker() {
        val marker = LatLng(37.570286992195, 126.98361037914)
        mMap.addMarker(MarkerOptions().position(marker).title("스타터스"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
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
