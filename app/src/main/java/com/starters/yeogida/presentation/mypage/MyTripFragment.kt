package com.starters.yeogida.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.data.local.LikeTripData
import com.starters.yeogida.databinding.FragmentMyTripBinding
import com.starters.yeogida.presentation.common.RegionCategory
import com.starters.yeogida.presentation.like.LikeTripAdapter
import com.starters.yeogida.presentation.like.LikeTripViewModel
import com.starters.yeogida.presentation.trip.AddTripActivity

class MyTripFragment : Fragment() {
    private lateinit var binding: FragmentMyTripBinding
    private val viewModel: LikeTripViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyTripBinding.inflate(inflater, container, false)
        binding.view = this

        return binding.root
    }
}
