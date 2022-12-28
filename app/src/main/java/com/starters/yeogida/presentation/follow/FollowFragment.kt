package com.starters.yeogida.presentation.follow

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.follow.FollowUserData
import com.starters.yeogida.data.remote.response.follow.FollowerUserResponse
import com.starters.yeogida.data.remote.response.follow.FollowingUserResponse
import com.starters.yeogida.databinding.FragmentFollowBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.user.profile.UserProfileActivity
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.regex.Pattern

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
        choice?.let {
            setPage(choice)
            initFollowerData()
            setSearchTextChangedListener(choice)
            setOpenUserProfile()
        }
    }

    private fun setSearchTextChangedListener(choice: Int) {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = binding.etSearch.text.toString()
                if (isValidSearchText(searchText)) {
                    Log.e("isValidSearch", isValidSearchText(searchText).toString())
                    when (choice) {
                        0 -> {
                            // 팔로워 검색
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = followService.searchFollower(
                                    dataStore.userBearerToken.first(),
                                    searchText
                                )

                                when (response.code()) {
                                    200 -> {
                                        setFollowerList(response)
                                    }
                                    else -> {
                                        Log.e("searchFollow", response.toString())
                                    }
                                }
                            }

                        }
                        1 -> {
                            // 팔로잉 검색
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = followService.searchFollowing(
                                    dataStore.userBearerToken.first(),
                                    searchText
                                )

                                when (response.code()) {
                                    200 -> {
                                        setFollowingList(response)
                                    }
                                    else -> {
                                        Log.e("searchFollow", response.toString())
                                    }
                                }
                            }
                        }
                    }
                }

                if (searchText == "") {
                    when (choice) {
                        0 -> {
                            // 팔로워 검색
                            initFollowerData()
                        }
                        1 -> {
                            // 팔로잉 검색
                            initFollowingData()
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun isValidSearchText(searchText: String): Boolean {
        val regex = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{1,8}\$"
        val pattern = Pattern.compile(regex)

        val matcher = pattern.matcher(searchText)

        return matcher.matches()
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
                    setFollowerList(followerUserResponse)
                }

                else -> {
                    Log.e("FollowerResponse", "팔로워 불러오기 실패 $followerUserResponse")
                    requireContext().shortToast("팔로워 불러오기 실패")
                }
            }
        }
    }

    private suspend fun setFollowerList(followerUserResponse: Response<BaseResponse<FollowerUserResponse>>) {
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

    private fun initFollowingData() {
        CoroutineScope(Dispatchers.IO).launch {
            val userBearerToken = dataStore.userBearerToken.first()

            // 팔로잉 목록
            val followingUserResponse = followService.getFollowingUser(
                userBearerToken
            )

            when (followingUserResponse.code()) {
                200 -> {
                    setFollowingList(followingUserResponse)
                }
                else -> {
                    Log.e("FollowerResponse", "팔로잉 목록 불러오기 실패 $followingUserResponse")
                    requireContext().shortToast("팔로잉 목록 불러오기 실패")
                }
            }
        }
    }

    private suspend fun setFollowingList(followingUserResponse: Response<BaseResponse<FollowingUserResponse>>) {
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

    private fun setOpenUserProfile() {
        viewModel.openUserProfileEvent.observe(viewLifecycleOwner, EventObserver { memberId ->
            Intent(requireContext(), UserProfileActivity::class.java).apply {
                putExtra("memberId", memberId)
                startActivity(this)
            }
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