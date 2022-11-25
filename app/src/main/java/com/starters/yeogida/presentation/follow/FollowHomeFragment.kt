package com.starters.yeogida.presentation.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.starters.yeogida.databinding.FragmentFollowHomeBinding

class FollowHomeFragment : Fragment() {

    private lateinit var binding: FragmentFollowHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPagerAdapter()
    }

    private fun setViewPagerAdapter() {
        val pagerAdapter = PagerFragmentStateAdapter(requireActivity())
        pagerAdapter.addFragment(FollowerFragment())
        pagerAdapter.addFragment(FollowingFragment())

        binding.viewpagerFollow.adapter = pagerAdapter

        binding.viewpagerFollow.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        TabLayoutMediator(binding.tablayoutFollow, binding.viewpagerFollow) { tab, position ->
            when (position) {
                0 -> tab.text = "팔로워"
                1 -> tab.text = "팔로잉"
            }
        }.attach()
    }
}