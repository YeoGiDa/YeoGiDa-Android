package com.starters.yeogida.presentation.user.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentUserProfileBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.EventObserver
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

    private val dataStore = YeogidaApplication.getInstance().getDataStore()

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

        initUserProfile()
        setOnBackPressed()
        onFollowBtnClicked()
    }

    private fun setOnBackPressed() {
        binding.tbUserProfile.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initUserProfile() {
        requireActivity().intent?.extras?.let {

            CoroutineScope(Dispatchers.IO).launch {
                val response = userService.getUserProfile(
                    dataStore.userBearerToken.first(),
                    it.getLong("memberId")
                )

                when (response.code()) {
                    200 -> {
                        withContext(Dispatchers.Main) {
                            binding.userProfile = response.body()?.data
                            binding.executePendingBindings()
                        }
                    }
                    else -> {
                        Log.e("UserProfile", "$response")
                    }
                }
            }
        }
    }

    private fun onFollowBtnClicked() {
        viewModel.followUserEvent.observe(viewLifecycleOwner, EventObserver { memberId ->
            val isFollow = binding.btnUserProfileFollow.isSelected

            if (isFollow) {
                // 이미 팔로우가 되어있을 경우 눌렸을 때 -> 팔로우 취소
                CoroutineScope(Dispatchers.IO).launch {
                    val response = followService.deleteFollowing(
                        dataStore.userBearerToken.first(),
                        memberId
                    )
                    withContext(Dispatchers.Main) {
                        when (response.code()) {
                            200 -> {
                                // 팔로우 취소 성공
                                with(binding.btnUserProfileFollow) {
                                    isSelected = false
                                    text = "팔로우"
                                    setTextColor(resources.getColor(R.color.white, null))
                                }
                            }

                            else -> {
                                Log.e("deleteFollowing", "$response")
                                requireContext().shortToast("팔로우 취소 실패")
                            }
                        }
                    }
                }
            } else {
                // 팔로우가 안되어 있는 경우 눌렸을 때 -> 팔로우 추가
                CoroutineScope(Dispatchers.IO).launch {
                    val response = followService.addFollowing(
                        dataStore.userBearerToken.first(),
                        memberId
                    )

                    withContext(Dispatchers.Main) {
                        when (response.code()) {
                            200 -> {
                                // 팔로우 추가 성공
                                with(binding.btnUserProfileFollow) {
                                    isSelected = true
                                    text = "팔로잉"
                                    setTextColor(resources.getColor(R.color.black, null))
                                }
                            }

                            else -> {
                                Log.e("addFollowing", "$response")
                                requireContext().shortToast("팔로우 추가 실패")
                            }
                        }
                    }
                }
            }
        })

    }
}