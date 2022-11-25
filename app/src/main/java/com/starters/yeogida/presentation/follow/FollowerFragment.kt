package com.starters.yeogida.presentation.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.starters.yeogida.databinding.FragmentFollowerBinding

class FollowerFragment : Fragment() {

    private lateinit var binding: FragmentFollowerBinding
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val followAdapter = FollowAdapter()
        binding.rvFollow.adapter = followAdapter

        userList.add(
            User(
                "https://user-images.githubusercontent.com/20774764/153292680-cac43d23-3621-4cce-97b0-38db57d60aa0.png",
                "user1"
            )
        )
        userList.add(
            User(
                "https://user-images.githubusercontent.com/20774764/153292685-e8ccb8df-cd94-4135-b472-c3d48e477202.png",
                "user2"
            )
        )

        followAdapter.submitUserList(userList)
    }
}