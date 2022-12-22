package com.starters.yeogida.presentation.around

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.starters.yeogida.R
import com.starters.yeogida.data.local.PlaceMapData
import com.starters.yeogida.data.remote.response.place.PlaceMapList
import com.starters.yeogida.databinding.FragmentPlaceMapBottomSheetBinding

class PlaceMapBottomSheetFragment(private val placeList: PlaceMapList) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPlaceMapBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_place_map_bottom_sheet,
            container,
            false
        )

        initAdapter()
        return binding.root
    }

    private fun initAdapter() {
        binding.place = PlaceMapData(
            placeList.imgUrl,
            placeList.title,
            placeList.star,
            placeList.commentCount,
            placeList.tag,
            placeList.address
        )
    }
}
