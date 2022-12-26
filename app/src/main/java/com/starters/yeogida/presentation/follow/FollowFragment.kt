package com.starters.yeogida.presentation.follow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.response.follow.FollowUserData
import com.starters.yeogida.databinding.FragmentFollowBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowFragment : Fragment() {

    private lateinit var binding: FragmentFollowBinding
    private val viewModel: FollowViewModel by viewModels()
    private val dataStore = YeogidaApplication.getInstance().getDataStore()
    private val followService = YeogidaClient.followService

    companion object {
        val FOLLOW_CATEGORY_ITEM = "FOLLOW_CATEGORY_ITEM"

        @JvmStatic
        fun newInstance(position: Int) =
            FollowFragment().apply {
                arguments = Bundle().apply {
                    putInt(FOLLOW_CATEGORY_ITEM, position)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        Log.e("FollowFragment", "onResume()")
        val choice = arguments?.getInt(FOLLOW_CATEGORY_ITEM)
        setPage(choice)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        val choice = arguments?.getInt(FOLLOW_CATEGORY_ITEM)
        setPage(choice)
        initFollowerData()
        setOpenUserProfile()
    }


    private fun initFollowerData() {
        CoroutineScope(Dispatchers.IO).launch {
            val userBearerToken = dataStore.userBearerToken.first()

            // 팔로워 목록
            val followerUserResponse = followService.getFollowerUser(
                userBearerToken
            )
            when (followerUserResponse.code()) {
                200 -> {
                    val followerList = followerUserResponse.body()?.data?.followerList

                    followerList?.let {
                        with(FollowLists.follower) {
                            clear()
                            for (i in followerList.indices) {
                                add(
                                    FollowUserData(
                                        followerList[i].memberId,
                                        followerList[i].nickname,
                                        followerList[i].imgUrl
                                    )
                                )
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        binding.rvFollow.adapter?.notifyDataSetChanged()
                    }
                }

                else -> {
                    Log.e("FollowerResponse", "팔로워 불러오기 실패 $followerUserResponse")
                    requireContext().shortToast("팔로워 불러오기 실패")
                }
            }
        }
    }

    private fun initFollowingData() {
        CoroutineScope(Dispatchers.IO).launch {
            val userBearerToken = dataStore.userBearerToken.first()

            // 팔로잉 목록
            val followingUserResponse = followService.getFollowingUser(
                userBearerToken
            )

            when (followingUserResponse.code()) {
                200 -> {
                    val followingList = followingUserResponse.body()?.data?.followingList

                    followingList?.let {
                        with(FollowLists.following) {
                            clear()
                            for (i in followingList.indices) {
                                add(
                                    FollowUserData(
                                        followingList[i].memberId,
                                        followingList[i].nickname,
                                        followingList[i].imgUrl
                                    )
                                )
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        binding.rvFollow.adapter?.notifyDataSetChanged()
                    }
                }
                else -> {
                    Log.e("FollowerResponse", "팔로잉 목록 불러오기 실패 $followingUserResponse")
                    requireContext().shortToast("팔로잉 목록 불러오기 실패")
                }
            }
        }
    }

    private fun setOpenUserProfile() {
        viewModel.openUserProfileEvent.observe(viewLifecycleOwner, EventObserver { memberId ->
            findNavController().navigate(
                R.id.action_follow_to_userProfile,
                bundleOf("memberId" to memberId)
            )
        })
    }

    private fun setPage(choice: Int?) {
        choice?.let {
            when (choice) {
                0 -> {
                    setFollowAdapter(FollowLists.follower, choice)
                    initFollowerData()
                }
                1 -> {
                    setFollowAdapter(FollowLists.following, choice)
                    initFollowingData()
                }
                else -> {}
            }
        }
    }

    private fun setFollowAdapter(followUserResponseList: List<FollowUserData>, choice: Int) {

        with(binding.rvFollow) {
            adapter = FollowAdapter(followUserResponseList, viewModel).apply {
                itemClick = object : FollowAdapter.ItemClick {
                    override fun onDeleteBtnClick(view: View, user: FollowUserData) {
                        showDialog(choice, user)
                    }
                }
            }
        }
    }

    private fun showDialog(choice: Int, user: FollowUserData) {
        CustomDialog(requireContext()).apply {
            setTitle("${if (choice == 0) "팔로워" else "팔로잉"} 목록에서 삭제합니다.")
            setPositiveBtn("삭제") {
                deleteFollowUser(choice, user)
                dismissDialog()
            }
            setNegativeBtn("취소") {
                dismissDialog()
            }
            showDialog()
        }
    }

    private fun deleteFollowUser(choice: Int, user: FollowUserData) {
        if (choice == 0) {
            // 팔로워 삭제
            CoroutineScope(Dispatchers.IO).launch {
                val userBearerToken = dataStore.userBearerToken.first()

                val response = followService.deleteFollower(
                    userBearerToken,
                    user.memberId
                )

                when (response.code()) {
                    200 -> {
                        withContext(Dispatchers.Main) {
                            FollowLists.follower.remove(user)   // 목록에서 삭제
                            binding.rvFollow.adapter?.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        Log.e("deleteFollow", response.toString())
                    }
                }
            }
        } else {
            // 팔로잉 삭제
            CoroutineScope(Dispatchers.IO).launch {
                val userBearerToken = dataStore.userBearerToken.first()

                val response = followService.deleteFollowing(
                    userBearerToken,
                    user.memberId
                )

                when (response.code()) {
                    200 -> {
                        withContext(Dispatchers.Main) {
                            FollowLists.following.remove(user)   // 목록에서 삭제
                            binding.rvFollow.adapter?.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        Log.e("deleteFollow", response.toString())
                    }
                }
            }
        }
    }
}