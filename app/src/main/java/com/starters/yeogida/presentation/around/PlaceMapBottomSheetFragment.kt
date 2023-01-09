package com.starters.yeogida.presentation.around

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
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
        binding.view = this

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

        when (placeList.tag) {
            "식당" -> binding.tvAroundPlaceMapTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_red_17)
            "카페" -> binding.tvAroundPlaceMapTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_orange_17)
            "바" -> binding.tvAroundPlaceMapTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_yellow_17)
            "관광지" -> binding.tvAroundPlaceMapTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_green_17)
            "쇼핑" -> binding.tvAroundPlaceMapTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_blue_17)
            "숙소" -> binding.tvAroundPlaceMapTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_purple_17)
            "기타" -> binding.tvAroundPlaceMapTag.setBackgroundResource(R.drawable.rectangle_fill_gray200_17)
        }
    }

    fun moveToDetail(view: View) {
        findNavController().navigate(R.id.action_placeMap_to_placeDetail, bundleOf("placeId" to placeList.placeId))
        dismiss()
    }
}
