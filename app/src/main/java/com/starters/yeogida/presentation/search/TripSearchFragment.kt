package com.starters.yeogida.presentation.search

import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.local.SearchKeyword
import com.starters.yeogida.data.remote.response.trip.RankTrip
import com.starters.yeogida.databinding.FragmentTripSearchBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime


class TripSearchFragment : Fragment() {
    private lateinit var binding: FragmentTripSearchBinding
    private val searchService = YeogidaClient.searchService
    private val dataBase = YeogidaApplication.getInstance().getDataBase()

    private val rankList = mutableListOf<RankTrip>()

    private val viewModel: SearchViewModel by viewModels()

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

        binding.lifecycleOwner = viewLifecycleOwner

        setRankAdapter(rankList)
        initRecentChip()
        initPopularKeyword()
        setSearchListener()
        setOnBackPressed()
        setOnDeleteAllClickListener()
    }

    private fun setOnBackPressed() {
        binding.tbTripSearch.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    private fun setOnDeleteAllClickListener() {
        binding.btnTripSearchRemoveAll.setOnClickListener {
            deleteAllRecentChip()
        }
    }

    private fun setSearchListener() {
        with(binding) {
            etTripSearch.setOnEditorActionListener { textView, action, keyEvent ->
                var handled = false
                val searchKeyword = textView.text.toString()

                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    // 검색 결과 창으로 이동
                    if (!searchKeyword.isNullOrBlank()) {
                        moveToSearchResult(searchKeyword)
                        addRecentChip(searchKeyword)
                        handled = true
                    } else {
                        requireActivity().shortToast("검색어를 입력해주세요")
                    }
                }
                handled
            }

            ivTripSearch.setOnClickListener {
                val searchKeyword = etTripSearch.text.toString()
                if (!searchKeyword.isNullOrBlank()) {
                    moveToSearchResult(searchKeyword)
                    addRecentChip(searchKeyword)
                } else {
                    requireActivity().shortToast("검색어를 입력해주세요")
                }
            }
        }

        // viewModel Event 옵저빙
        viewModel.popularKeywordClickedEvent.observe(
            viewLifecycleOwner,
            EventObserver { searchKeyword ->
                if (searchKeyword != "-") {
                    moveToSearchResult(searchKeyword)
                    addRecentChip(searchKeyword)
                }
            })
    }

    private fun moveToSearchResult(searchKeyword: String) {
        binding.etTripSearch.text.clear()

        findNavController().navigate(
            R.id.action_tripSearch_to_tripSearchResult,
            bundleOf("searchKeyword" to searchKeyword)
        )
    }

    private fun initPopularKeyword() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = searchService.getPopularTrip()

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data

                    data?.let { data ->
                        rankList.clear()

                        // 10개보다 적으면
                        if (data.rankList.size < 10) {
                            rankList.addAll(data.rankList)
                            repeat(10 - rankList.size) {
                                rankList.add(
                                    RankTrip("-", 0.0)
                                )
                            }
                            Log.e("rankList", rankList.toString())
                            withContext(Dispatchers.Main) {
                                binding.rvSearchTrip.adapter?.notifyDataSetChanged()
                            }
                        } else {
                            rankList.addAll(data.rankList)
                            Log.e("rankList", rankList.toString())
                            withContext(Dispatchers.Main) {
                                binding.rvSearchTrip.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }

                else -> {
                    rankList.clear()
                    repeat(10) {
                        rankList.add(
                            RankTrip("-", 0.0)
                        )
                    }
                    withContext(Dispatchers.Main) {
                        binding.rvSearchTrip.adapter?.notifyDataSetChanged()
                    }
                }
            }


        }
    }

    private fun getScreenWidth(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    private fun setRankAdapter(rankList: List<RankTrip>) {
        val dpWidth = getScreenWidth()
        with(binding.rvSearchTrip) {
            adapter = PopularSearchAdapter(rankList, viewModel, dpWidth)
            layoutManager =
                object : GridLayoutManager(requireContext(), 5, RecyclerView.HORIZONTAL, false) {
                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
        }
    }

    private fun initRecentChip() {
        CoroutineScope(Dispatchers.IO).launch {
            val keywordList = dataBase.searchKeywordDao().getAllRecentSearch()

            withContext(Dispatchers.Main) {
                with(binding.chipGroupRecent) {
                    removeAllViews()    // 비우고
                    // 받아온 값 추가하기
                    for (keyword in keywordList) {
                        addView(
                            Chip(requireContext(), null, R.attr.recentSearchChipStyle).apply {
                                text = keyword.keyword

                                // 제거 버튼 클릭 시
                                setOnCloseIconClickListener {
                                    // 최근 검색어에서 제거
                                    // removeView(this)
                                    deleteRecentChip(text.toString())
                                    removeView(this)
                                }

                                setOnClickListener {
                                    // 검색 결과로 이동 + 제일 최근 검색어로 갱신
                                    moveToSearchResult(text.toString())
                                    addRecentChip(text.toString())
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun addRecentChip(searchKeyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dataBase.searchKeywordDao().insertKeyword(
                SearchKeyword(
                    searchKeyword,
                    ZonedDateTime.now()
                )
            )
        }
    }

    private fun deleteRecentChip(searchKeyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dataBase.searchKeywordDao().deleteKeyword(searchKeyword)
        }
    }

    private fun deleteAllRecentChip() {
        CoroutineScope(Dispatchers.IO).launch {
            dataBase.searchKeywordDao().deleteAllKeyword()
            withContext(Dispatchers.Main) {
                binding.chipGroupRecent.removeAllViews()
            }
        }
    }
}