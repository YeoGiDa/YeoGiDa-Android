package com.starters.yeogida.presentation.like

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class LikePagerFragmentStateAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private val NUM_PAGES = 18

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int) = LikeTripFragment.newInstance(position)
}