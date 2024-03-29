package com.starters.yeogida.presentation.place

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

class MoreBottomSheetFragment(val type: String, val isMyPost: Boolean, val itemClick: (String) -> Unit) :
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
        binding.tvBottomSheetTitle.text = "메뉴"
        binding.rvSort.adapter = sortAdapter
        if (isMyPost) {
            if (type == "trip") {
                sortAdapter.sortList.addAll(
                    listOf(
                        SortData("장소 지도로 보기"),
                        SortData("수정"),
                        SortData("삭제")
                    )
                )
            } else {
                sortAdapter.sortList.addAll(
                    listOf(
                        SortData("수정"),
                        SortData("삭제")
                    )
                )
            }
        } else {
            if (type == "trip") {
                sortAdapter.sortList.addAll(
                    listOf(
                        SortData("장소 지도로 보기"),
                        SortData("신고")
                    )
                )
            } else {
                sortAdapter.sortList.addAll(
                    listOf(
                        SortData("신고")
                    )
                )
            }
        }
        sortAdapter.notifyDataSetChanged()
    }
}