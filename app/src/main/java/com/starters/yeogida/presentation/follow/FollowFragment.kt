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
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.response.follow.FollowUserData
import com.starters.yeogida.databinding.FragmentFollowBinding
import com.starters.yeogida.presentation.common.EventObserver

class FollowFragment : Fragment() {

    private lateinit var binding: FragmentFollowBinding
    private val viewModel: FollowViewModel by viewModels()
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

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
        initFollowData()
        setOpenUserProfile()
    }

    private fun initFollowData() {
        TODO("Not yet implemented")
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
                0 -> setFollowAdapter(FollowLists.follower)
                1 -> setFollowAdapter(FollowLists.following)
                else -> {}
            }
        }
    }

    private fun setFollowAdapter(followUserResponseList: List<FollowUserData>) {
        binding.rvFollow.adapter = FollowAdapter(followUserResponseList, viewModel)
    }
}