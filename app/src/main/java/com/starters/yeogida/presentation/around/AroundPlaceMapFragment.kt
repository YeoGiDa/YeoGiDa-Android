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
import com.starters.yeogida.databinding.FragmentAroundPlaceMapBinding

class AroundPlaceMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mView: MapView
    private lateinit var binding: FragmentAroundPlaceMapBinding
    private lateinit var mMap: GoogleMap

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

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        createMarker()
    }

    private fun createMarker() {
        val marker = LatLng(37.570286992195, 126.98361037914)
        mMap.addMarker(MarkerOptions().position(marker).title("스타터스"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val bottomSheetDialog = PlaceMapBottomSheetFragment()
        bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        return true
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

    fun close(view: View) {
        findNavController().navigateUp()
    }
}
