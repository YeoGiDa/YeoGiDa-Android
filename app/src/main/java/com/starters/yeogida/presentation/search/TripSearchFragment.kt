package com.starters.yeogida.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.starters.yeogida.R
import com.starters.yeogida.databinding.FragmentTripSearchBinding

class TripSearchFragment : Fragment() {
    private lateinit var binding: FragmentTripSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTripSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.chipGroupRecent) {
            addView(
                Chip(requireContext(), null, R.attr.recentSearchChipStyle).apply {
                    text = "test"
                }
            )
        }
    }
}