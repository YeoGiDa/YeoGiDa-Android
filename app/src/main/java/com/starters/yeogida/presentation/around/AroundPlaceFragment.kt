package com.starters.yeogida.presentation.around

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.R
import com.starters.yeogida.data.local.PlaceData
import com.starters.yeogida.databinding.FragmentAroundPlaceBinding
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.presentation.trip.AddTripActivity

class AroundPlaceFragment : Fragment() {
    private lateinit var binding: FragmentAroundPlaceBinding
    private val viewModel: AroundPlaceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_around_place, container, false)
        binding.view = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initNavigation()
    }

    private fun initAdapter() {
        val aroundPlaceAdapter = AroundPlaceAdapter(viewModel)
        binding.rvAroundPlace.adapter = aroundPlaceAdapter
        aroundPlaceAdapter.aroundPlaceList.addAll(
            listOf(
                PlaceData("https://cdn.pixabay.com/photo/2016/04/26/03/55/salmon-1353598_1280.jpg", "연어맛집", "4.5", 30, "식당"),
                PlaceData("https://cdn.pixabay.com/photo/2013/04/11/19/46/building-102840_1280.jpg", "박물관", "2.0", 1, "카페"),
                PlaceData("https://cdn.pixabay.com/photo/2017/08/07/23/11/iceland-2608985_1280.jpg", "오오폭포", "3.0", 0, "바"),
                PlaceData("https://cdn.pixabay.com/photo/2016/11/21/13/04/alcoholic-beverages-1845295_1280.jpg", "수울수울", "1.5", 999, "관광지"),
                PlaceData("https://cdn.pixabay.com/photo/2013/04/11/19/46/building-102840_1280.jpg", "박물관", "2.0", 1, "카페"),
                PlaceData("https://cdn.pixabay.com/photo/2017/08/07/23/11/iceland-2608985_1280.jpg", "오오폭포", "3.0", 0, "바"),
                PlaceData("https://cdn.pixabay.com/photo/2016/04/26/03/55/salmon-1353598_1280.jpg", "연어맛집", "4.5", 30, "쇼핑"),
                PlaceData("https://cdn.pixabay.com/photo/2013/04/11/19/46/building-102840_1280.jpg", "박물관", "2.0", 1, "숙소"),
                PlaceData("https://cdn.pixabay.com/photo/2017/08/07/23/11/iceland-2608985_1280.jpg", "오오폭포", "3.0", 0, "기타")
            )
        )
        aroundPlaceAdapter.notifyDataSetChanged()
    }

    private fun initNavigation() {
        viewModel.openPlaceDetailEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_aroundPlace_to_placeDetail)
        }
    }

    fun moveToPlaceMap(view: View) {
        findNavController().navigate(R.id.action_aroundPlace_to_placeMap)
    }

    fun close(view: View) {
        (activity as PlaceActivity).finish()
    }
}
