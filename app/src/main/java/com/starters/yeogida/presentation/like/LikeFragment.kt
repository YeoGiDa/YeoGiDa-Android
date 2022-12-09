package com.starters.yeogida.presentation.like

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.starters.yeogida.databinding.FragmentLikeBinding
import com.starters.yeogida.presentation.common.RegionCategory

class LikeFragment : Fragment() {
    private lateinit var binding: FragmentLikeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewpagerLike.apply {
            adapter = LikePagerFragmentStateAdapter(context as FragmentActivity)
        }

        TabLayoutMediator(binding.tabLike, binding.viewpagerLike) { tab, position ->
            tab.text = RegionCategory.values()[position].locName
        }.attach()
    }
}