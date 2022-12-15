package com.starters.yeogida.presentation.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.starters.yeogida.R
import com.starters.yeogida.data.local.SortData
import com.starters.yeogida.databinding.FragmentBottomSheetSortBinding
import com.starters.yeogida.presentation.common.SortAdapter

class PlaceSortBottomSheetFragment(val itemClick: (String) -> Unit) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetSortBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_sort, container, false)

        initAdapter()

        return binding.root
    }

    override fun getTheme(): Int = R.style.BottomSheetDialog

    private fun initAdapter() {
        val sortAdapter = SortAdapter {
            itemClick(it)
            dismiss()
        }
        binding.rvSort.adapter = sortAdapter
        sortAdapter.sortList.addAll(
            listOf(
                SortData("최신순"),
                SortData("별점순"),
                SortData("댓글 많은순")
            )
        )
        sortAdapter.notifyDataSetChanged()
    }
}
