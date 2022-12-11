package com.starters.yeogida.presentation.follow

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FollowPagerFragmentStateAdapter(fragmentActivity: FragmentActivity, numPages: Int) :
    FragmentStateAdapter(fragmentActivity) {
    private val NUM_PAGES = numPages

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int) = FollowFragment.newInstance(position)
}