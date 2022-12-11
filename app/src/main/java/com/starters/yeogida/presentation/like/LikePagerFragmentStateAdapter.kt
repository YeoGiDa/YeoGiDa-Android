package com.starters.yeogida.presentation.like

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class LikePagerFragmentStateAdapter(fragmentActivity: FragmentActivity, numPages: Int) :
    FragmentStateAdapter(fragmentActivity) {
    private val NUM_PAGES = numPages

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int) = LikeTripFragment.newInstance(position)
}