package com.starters.yeogida.presentation.user.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.response.common.TripResponse
import com.starters.yeogida.databinding.FragmentUserProfileBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.around.TripSortBottomSheetFragment
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private val viewModel: UserProfileViewModel by viewModels()

    private val userService = YeogidaClient.userService
    private val followService = YeogidaClient.followService
    private val tripService = YeogidaClient.tripService

    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private var memberId: Long = 0
    private var region: String = "nothing"
    private var sortValue: String = "id"

    private val tripList = mutableListOf<TripResponse>()
    private val regionSet = mutableSetOf<String>()
    private var isFollow = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        getMemberId()
        setOnBackPressed()
        initUserProfile()
        setOnFollowBtnClicked()

        setTripAdapter()
        setOnTripClicked()
        initUserTripList()
        initBottomSheet()
        initChipClickListener()
    }

    override fun onResume() {
        super.onResume()
        initUserProfile()
    }

    private fun setOnTripClicked() {
        viewModel.openAroundPlaceEvent.observe(viewLifecycleOwner, EventObserver { tripId ->
            Intent(requireContext(), PlaceActivity::class.java).apply {
                putExtra("tripId", tripId)
                startActivity(this)
            }
        })
    }

    private fun getMemberId() {
        requireActivity().intent?.extras?.let { bundle ->
            memberId = bundle.getLong("memberId")
        }
    }

    private fun setTripAdapter() {
        binding.rvUserProfileTrip.adapter = UserProfileAdapter(tripList, viewModel)
    }

    // chip button 클릭 시
    private fun initChipClickListener() {
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedId ->
            if (checkedId.isEmpty()) {
                region = "nothing"
                getSortedData()
            } else {
                val selectedChipText =
                    binding.chipGroup.findViewById<Chip>(binding.chipGroup.checkedChipId).text.toString()
                region = selectedChipText
                getSortedData()
            }
        }
    }

    private fun addRegionChip(region: String) {
        with(binding.chipGroup) {
            addView(Chip(requireContext(), null, R.attr.regionChipStyle).apply {
                text = region
            })
        }
    }

    private fun setOnBackPressed() {
        binding.tbUserProfile.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initBottomSheet() {
        binding.btnUserProfileSort.setOnClickListener {
            val bottomSheetDialog = TripSortBottomSheetFragment {
                binding.btnUserProfileSort.text = it
                when (it) {
                    "최신순" -> sortValue = "id"
                    "인기순" -> sortValue = "heart"
                }
                getSortedData()
            }
            bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        }
    }

    private fun initUserProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = userService.getUserProfile(
                dataStore.userBearerToken.first(),
                memberId
            )

            when (response.code()) {
                200 -> {
                    withContext(Dispatchers.Main) {
                        binding.userProfile = response.body()?.data
                        isFollow = response.body()?.data!!.isFollow
                        binding.executePendingBindings()
                    }
                }
                else -> {
                    Log.e("UserProfile", "$response")
                }
            }
        }
    }

    private fun initUserTripList() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.getUserTripList(
                memberId,
                "nothing",
                "id"
            )

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let { data ->
                        tripList.clear()
                        tripList.addAll(data.tripList)

                        for (trip in tripList) {
                            regionSet.add(trip.region)
                        }

                        withContext(Dispatchers.Main) {
                            for (region in regionSet) {
                                addRegionChip(region)
                            }
                            binding.btnUserProfileSort.text = "최신순"
                            binding.rvUserProfileTrip.adapter?.notifyDataSetChanged()
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    private fun getSortedData() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("sortValue", sortValue)
            Log.e("region", region)
            val response = tripService.getUserTripList(
                memberId,
                region,
                sortValue
            )

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let { data ->
                        tripList.clear()
                        tripList.addAll(data.tripList)

                        withContext(Dispatchers.Main) {
                            when (sortValue) {
                                "id" -> binding.btnUserProfileSort.text = "최신순"
                                "heart" -> binding.btnUserProfileSort.text = "인기순"
                            }
                            binding.rvUserProfileTrip.adapter?.notifyDataSetChanged()

                        }
                    }
                }
                else -> {

                }
            }
        }
    }


    private fun setOnFollowBtnClicked() {
        viewModel.followUserEvent.observe(viewLifecycleOwner, EventObserver { memberId ->
            if (isFollow) {
                // 이미 팔로우가 되어있을 경우 눌렸을 때 -> 팔로우 취소
                with(binding.btnUserProfileFollow) {
                    isSelected = false
                    text = "팔로우"
                    setTextColor(resources.getColor(R.color.white, null))
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val response = followService.deleteFollowing(
                        dataStore.userBearerToken.first(),
                        memberId
                    )
                    withContext(Dispatchers.Main) {
                        when (response.code()) {
                            200 -> {
                                // 팔로우 취소 성공
                                isFollow = false
                                initUserProfile()
                            }

                            else -> {
                                Log.e("deleteFollowing", "$response")
                                with(binding.btnUserProfileFollow) {
                                    isSelected = true
                                    text = "팔로잉"
                                    setTextColor(resources.getColor(R.color.black, null))
                                }
                                initUserProfile()
                                requireContext().shortToast("팔로우 취소 실패")
                            }
                        }
                    }
                }
            } else {
                // 팔로우가 안되어 있는 경우 눌렸을 때 -> 팔로우 추가
                with(binding.btnUserProfileFollow) {
                    isSelected = true
                    text = "팔로잉"
                    setTextColor(resources.getColor(R.color.black, null))
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val response = followService.addFollowing(
                        dataStore.userBearerToken.first(),
                        memberId
                    )

                    withContext(Dispatchers.Main) {
                        when (response.code()) {
                            200 -> {
                                // 팔로우 추가 성공
                                isFollow = true
                                initUserProfile()
                            }

                            else -> {
                                Log.e("addFollowing", "$response")
                                with(binding.btnUserProfileFollow) {
                                    isSelected = false
                                    text = "팔로우"
                                    setTextColor(resources.getColor(R.color.white, null))
                                }
                                initUserProfile()
                                requireContext().shortToast("팔로우 추가 실패")
                            }
                        }
                    }
                }
            }
        })

    }
}