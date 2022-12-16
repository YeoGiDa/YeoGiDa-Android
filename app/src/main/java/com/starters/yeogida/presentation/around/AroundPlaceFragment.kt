package com.starters.yeogida.presentation.around

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.GlideApp
import com.starters.yeogida.R
import com.starters.yeogida.databinding.FragmentAroundPlaceBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.place.AddPlaceActivity
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.presentation.trip.PlaceSortBottomSheetFragment
import com.starters.yeogida.util.customEnqueue

class AroundPlaceFragment : Fragment() {
    private lateinit var binding: FragmentAroundPlaceBinding
    private val viewModel: AroundPlaceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_around_place, container, false)
        binding.view = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlaceNetwork("id")
        initNavigation()
        initBottomSheet()
        with(binding.layoutCollapsingAroundPlace) {
            title = "일이삼사오육칠팔구십"
        }
    }

    private fun initBottomSheet() {
        binding.btnAroundPlaceSort.text = "최신순"

        binding.btnAroundPlaceSort.setOnClickListener {
            val bottomSheetDialog = PlaceSortBottomSheetFragment {
                binding.btnAroundPlaceSort.text = it
            }
            bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        }
    }

    private fun initPlaceNetwork(condition: String) {
        val aroundPlaceAdapter = AroundPlaceAdapter(viewModel)
        binding.rvAroundPlace.adapter = aroundPlaceAdapter
        YeogidaClient.placeService.getPlaceList(
            2,
            condition
        ).customEnqueue(
            onSuccess = { responseData ->
                if (responseData.code == 200) {
                    responseData.data?.let { data -> aroundPlaceAdapter.aroundPlaceList.addAll(data.placeList) }
                    aroundPlaceAdapter.notifyDataSetChanged()
                }
            }
        )

        GlideApp.with(binding.ivAroundPlaceTrip)
            .load("https://cdn.pixabay.com/photo/2018/02/17/13/08/the-body-of-water-3159920__480.jpg")
            .into(binding.ivAroundPlaceTrip)
    }

    private fun initNavigation() {
        binding.tbAroundPlace.setNavigationOnClickListener {
            (activity as PlaceActivity).finish()
        }

        viewModel.openPlaceDetailEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_aroundPlace_to_placeDetail)
        }
    }

    fun moveToPlaceMap(view: View) {
        findNavController().navigate(R.id.action_aroundPlace_to_placeMap)
    }

    fun moveToAddPlace(view: View) {
        val intent = Intent(requireContext(), AddPlaceActivity::class.java)
        startActivity(intent)
    }

    fun moveToTop(view: View) {
        binding.scrollViewAroundPlace.smoothScrollTo(0, 0)
    }
}
