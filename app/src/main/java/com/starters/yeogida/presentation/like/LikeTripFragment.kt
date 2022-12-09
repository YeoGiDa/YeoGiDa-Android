package com.starters.yeogida.presentation.like

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.starters.yeogida.databinding.FragmentLikeTripBinding

class LikeTripFragment : Fragment() {
    private lateinit var binding: FragmentLikeTripBinding

    companion object {
        val REGION_CATEGORY_ITEM = "region_category_item"

        @JvmStatic
        fun newInstance(position: Int) =
            LikeTripFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION_CATEGORY_ITEM, position)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikeTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        val choice = arguments?.getInt(REGION_CATEGORY_ITEM)

        choice?.let {
            when (choice) {
                0 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.all)
                1 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.seoul)
                2 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.gyeonggi)
                3 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.all)
                4 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.seoul)
                5 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.gyeonggi)
                6 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.all)
                7 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.seoul)
                8 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.gyeonggi)
                9 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.all)
                10 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.seoul)
                11 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.gyeonggi)
                12 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.all)
                13 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.seoul)
                14 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.gyeonggi)
                15 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.all)
                16 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.seoul)
                17 -> binding.rvTrip.adapter = LikeTripAdapter(LikeTripLists.gyeonggi)
                else -> {}
            }
        }
    }
}