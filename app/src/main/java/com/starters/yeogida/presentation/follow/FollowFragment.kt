package com.starters.yeogida.presentation.follow

import android.content.Context
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
import com.starters.yeogida.data.remote.response.follow.FollowUserData
import com.starters.yeogida.databinding.FragmentFollowBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.user.profile.UserProfileActivity
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class FollowFragment : Fragment() {
    private lateinit var binding: FragmentFollowBinding
    private val viewModel: FollowViewModel by viewModels()
    private val followService = YeogidaClient.followService
    private lateinit var mContext: Context

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
    
    override fun onResume() {
        super.onResume()
        Log.e("FollowFragment", "onResume()")
        val choice = arguments?.getInt(FOLLOW_CATEGORY_ITEM)
        setPage(choice)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        binding.view = this
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        val choice = arguments?.getInt(FOLLOW_CATEGORY_ITEM)
        choice?.let {
            setPage(choice)
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
                                    searchText
                                )

                                when (response.code()) {
                                    200 -> {
                                        val followerList = response.body()?.data?.followerList
                                        setFollowerList(followerList)
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
                                    searchText
                                )

                                when (response.code()) {
                                    200 -> {
                                        val followingList = response.body()?.data?.followingList
                                        setFollowingList(followingList)
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
            // 팔로워 목록
            val followerUserResponse = followService.getFollowerUser()
            when (followerUserResponse.code()) {
                200 -> {
                    val followerList = followerUserResponse.body()?.data?.followerList
                    setFollowerList(followerList)
                }

                else -> {
                    Log.e("FollowerResponse", "팔로워 불러오기 실패 $followerUserResponse")
                    mContext.shortToast("팔로워 불러오기 실패")
                }
            }
        }
    }

    private suspend fun setFollowerList(followerList: List<FollowUserData>?) {
        followerList?.let {
            FollowLists.follower.clear()
            FollowLists.follower.addAll(followerList)
        }

        withContext(Dispatchers.Main) {
            binding.rvFollow.adapter?.notifyDataSetChanged()
            setEmptyView(
                FollowLists.follower,
                "아직 회원님을 팔로우한 사람이 없어요!"
            )
        }
    }

    private fun initFollowingData() {
        CoroutineScope(Dispatchers.IO).launch {
            // 팔로잉 목록
            val followingUserResponse = followService.getFollowingUser()

            when (followingUserResponse.code()) {
                200 -> {
                    val followingList = followingUserResponse.body()?.data?.followingList
                    setFollowingList(followingList)
                }
                else -> {
                    Log.e("FollowerResponse", "팔로잉 목록 불러오기 실패 $followingUserResponse")
                    mContext.shortToast("팔로잉 목록 불러오기 실패")
                }
            }
        }
    }

    private suspend fun setFollowingList(followingList: List<FollowUserData>?) {
        followingList?.let {
            FollowLists.following.clear()
            FollowLists.following.addAll(followingList)
        }

        withContext(Dispatchers.Main) {
            binding.rvFollow.adapter?.notifyDataSetChanged()
            setEmptyView(
                FollowLists.following,
                "아직 팔로잉한 사람이 없어요\n" + "사람들을 팔로잉 해보세요!"
            )
        }
    }

    private fun setOpenUserProfile() {
        viewModel.openUserProfileEvent.observe(
            viewLifecycleOwner,
            EventObserver { memberId ->
                Intent(mContext, UserProfileActivity::class.java).apply {
                    putExtra("memberId", memberId)
                    startActivity(this)
                }
            }
        )
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

    private fun setEmptyView(list: List<FollowUserData>, message: String) {
        with(binding) {
            if (list.isEmpty()) {
                etSearch.visibility = View.GONE
                rvFollow.visibility = View.GONE
                layoutFollowEmpty.visibility = View.VISIBLE
                binding.tvFollowEmpty.text = message
            } else {
                etSearch.visibility = View.VISIBLE
                rvFollow.visibility = View.VISIBLE
                layoutFollowEmpty.visibility = View.GONE
                binding.tvFollowEmpty.text = message
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
        CustomDialog(mContext).apply {
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
                val response = followService.deleteFollower(
                    user.memberId
                )

                when (response.code()) {
                    200 -> {
                        withContext(Dispatchers.Main) {
                            FollowLists.follower.remove(user) // 목록에서 삭제
                            setEmptyView(
                                FollowLists.follower,
                                "아직 회원님을 팔로우한 사람이 없어요!"
                            )
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
                val response = followService.deleteFollowing(
                    user.memberId
                )

                when (response.code()) {
                    200 -> {
                        withContext(Dispatchers.Main) {
                            FollowLists.following.remove(user) // 목록에서 삭제
                            setEmptyView(
                                FollowLists.following,
                                "아직 팔로잉한 사람이 없어요\n" + "사람들을 팔로잉 해보세요!"
                            )
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

    fun moveToSearchFriend(view: View) {
        startActivity(Intent(mContext, SearchFriendActivity::class.java))
    }
}
