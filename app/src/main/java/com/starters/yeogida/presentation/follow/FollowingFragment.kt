package com.starters.yeogida.presentation.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.starters.yeogida.databinding.FragmentFollowingBinding

class FollowingFragment : Fragment() {

    private lateinit var binding: FragmentFollowingBinding
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val followAdapter = FollowAdapter()
        binding.rvFollow.adapter = followAdapter

        userList.add(
            User(
                "https://user-images.githubusercontent.com/20774764/153292676-d3b24c31-f4ba-4273-9233-a0c77f835fd7.png",
                "user3"
            )
        )
        userList.add(
            User(
                "https://user-images.githubusercontent.com/20774764/153292675-78a7108b-644e-4836-ad30-dd261d998e4c.png",
                "user4"
            )
        )

        followAdapter.submitUserList(userList)
    }
}