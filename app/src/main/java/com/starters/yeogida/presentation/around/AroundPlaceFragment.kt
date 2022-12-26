package com.starters.yeogida.presentation.around

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.starters.yeogida.GlideApp
import com.starters.yeogida.R
import com.starters.yeogida.databinding.FragmentAroundPlaceBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.presentation.trip.PlaceSortBottomSheetFragment
import com.starters.yeogida.util.customEnqueue

class AroundPlaceFragment : Fragment() {
    private lateinit var binding: FragmentAroundPlaceBinding
    private val viewModel: AroundPlaceViewModel by viewModels()
    private var sortValue: String = "id"
    private var tagValue: String = "nothing"
    private var tripId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_around_place, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.view = this
        getTripId()
        initPlaceList()
        initNavigation()
        initBottomSheet()
        initChipClickListener()
        setOpenPlaceDetail()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun getTripId() {
        // 여행지 추가 후 tripId 받아오기
        val args = requireActivity().intent?.extras?.let { AroundPlaceFragmentArgs.fromBundle(it) }
        args?.tripId?.let { tripId = it }

        // 장소 추가 후 tripId 받아오기

        // 둘러보기 - 장소 상세 - 장소 목록 - 장소 추가

        // 여기 좋아 - 장소 목록 - 장소 추가
        initTripData()
    }

    private fun initTripData() {
        YeogidaClient.tripService.getTripInfo(
            tripId
        ).customEnqueue(
            onSuccess = {
                if (it.code == 200) {
                    with(binding) {
                        layoutCollapsingAroundPlace.title = it.data?.title
                        GlideApp.with(ivAroundPlaceTrip)
                            .load(it.data?.imgUrl)
                            .into(ivAroundPlaceTrip)
                        tvAroundPlacePlaceCount.text = it.data?.placeCount.toString()
                        tvAroundPlaceTripLikeCount.text = it.data?.heartCount.toString()
                    }
                }
            }
        )
    }

    private fun initBottomSheet() {
        binding.btnAroundPlaceSort.setOnClickListener {
            val bottomSheetDialog = PlaceSortBottomSheetFragment {
                binding.btnAroundPlaceSort.text = it
                when (it) {
                    "최신순" -> sortValue = "id"
                    "별점순" -> sortValue = "star"
                    "댓글 많은순" -> sortValue = "comment"
                }
                initPlaceList()
            }
            bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        }
    }

    // 정렬, 태그에 따른 장소 리스트
    private fun initPlaceList() {
        val aroundPlaceAdapter = AroundPlaceAdapter(viewModel)
        binding.rvAroundPlace.adapter = aroundPlaceAdapter
        YeogidaClient.placeService.getPlaceTagList(
            tripId,
            tagValue,
            sortValue
        ).customEnqueue(
            onSuccess = { responseData ->
                if (responseData.code == 200) {
                    if (sortValue == "id" && responseData.data?.placeList?.isEmpty() == true) {
                        with(binding) {
                            rvAroundPlace.visibility = View.GONE
                            layoutAroundPlaceTop.visibility = View.GONE
                            layoutAroundPlaceEmpty.visibility = View.VISIBLE
                            ivAroundPlaceMap.visibility = View.INVISIBLE
                        }
                    } else {
                        responseData.data?.let { data ->
                            aroundPlaceAdapter.aroundPlaceList.addAll(
                                data.placeList
                            )
                        }
                        when (sortValue) {
                            "id" -> binding.btnAroundPlaceSort.text = "최신순"
                            "star" -> binding.btnAroundPlaceSort.text = "별점순"
                            "comment" -> binding.btnAroundPlaceSort.text = "댓글 많은순"
                        }
                        aroundPlaceAdapter.notifyDataSetChanged()
                    }
                }
            }
        )
    }

    private fun initNavigation() {
        binding.tbAroundPlace.setNavigationOnClickListener {
            (activity as PlaceActivity).finish()
        }
    }

    fun setOpenPlaceDetail() {
        viewModel.openPlaceDetailEvent.observe(viewLifecycleOwner, EventObserver { placeId ->
            Log.e("placeId", placeId.toString())
            findNavController().navigate(
                R.id.action_aroundPlace_to_placeDetail, bundleOf(
                    "placeId" to placeId
                )
            )
        })
    }

    // chip button 클릭 시
    private fun initChipClickListener() {
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedId ->
            if (checkedId.isEmpty()) {
                tagValue = "nothing"
                initPlaceList()
            } else {
                val selectedChipText =
                    binding.chipGroup.findViewById<Chip>(binding.chipGroup.checkedChipId).text.toString()
                tagValue = selectedChipText
                initPlaceList()
            }
        }
    }

    fun moveToPlaceMap(view: View) {
        findNavController().navigate(R.id.action_aroundPlace_to_placeMap, bundleOf("tripId" to tripId))
    }

    fun moveToAddPlace(view: View) {
        findNavController().navigate(R.id.action_aroundPlace_to_addPlace, bundleOf("tripId" to tripId))
    }

    fun moveToTop(view: View) {
        binding.scrollViewAroundPlace.smoothScrollTo(0, 0)
    }
}
