package com.starters.yeogida.presentation.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.R
import com.starters.yeogida.data.local.FollowUserData
import com.starters.yeogida.databinding.FragmentFollowBinding
import com.starters.yeogida.presentation.common.EventObserver

class FollowFragment : Fragment() {

    private lateinit var binding: FragmentFollowBinding
    private val viewModel: FollowViewModel by viewModels()

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

        viewModel.openUserProfileEvent.observe(viewLifecycleOwner, EventObserver { memberId ->
            findNavController().navigate(
                R.id.action_follow_to_userProfile,
                bundleOf("memberId" to memberId)
            )
        })

        FollowLists.follower.addAll(
            listOf(
                FollowUserData(
                    memberId = requireArguments().getLong("memberId"),
                    "https://user-images.githubusercontent.com/20774764/153292680-cac43d23-3621-4cce-97b0-38db57d60aa0.png",
                    "user1"
                ),
                FollowUserData(
                    memberId = requireArguments().getLong("memberId"),
                    "https://user-images.githubusercontent.com/20774764/153292685-e8ccb8df-cd94-4135-b472-c3d48e477202.png",
                    "user2"
                )
            )
        )

        FollowLists.following.addAll(
            listOf(
                FollowUserData(
                    memberId = requireArguments().getLong("memberId"),
                    "https://user-images.githubusercontent.com/20774764/153292676-d3b24c31-f4ba-4273-9233-a0c77f835fd7.png",
                    "user3"
                ),
                FollowUserData(
                    memberId = requireArguments().getLong("memberId"),
                    "https://user-images.githubusercontent.com/20774764/153292675-78a7108b-644e-4836-ad30-dd261d998e4c.png",
                    "user4"
                )
            )
        )
    }

    private fun setPage(choice: Int?) {
        choice?.let {
            when (choice) {
                0 -> setFollowAdapter(FollowLists.follower)
                1 -> setFollowAdapter(FollowLists.following)
                else -> {}
            }
        }
    }

    private fun setFollowAdapter(followUserDataList: List<FollowUserData>) {
        binding.rvFollow.adapter = FollowAdapter(followUserDataList, viewModel)
    }
}