package com.starters.yeogida.presentation.around

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.starters.yeogida.data.local.TripLikeUserData
import com.starters.yeogida.databinding.FragmentTripLikeUserBinding

class TripLikeUserFragment : Fragment() {

    private val viewModel: AroundPlaceViewModel by viewModels()
    private lateinit var binding: FragmentTripLikeUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTripLikeUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userList = mutableListOf<TripLikeUserData>()
        // TODO. userList에 여행지 좋아요 누른 유저 목록 담기

        TripLikeUserListAdapter(userList, viewModel).apply {
            binding.rvTripLikeUser.adapter = this
        }
    }
}