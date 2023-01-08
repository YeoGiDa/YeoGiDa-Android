package com.starters.yeogida.presentation.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.starters.yeogida.R
import com.starters.yeogida.data.remote.response.trip.RankTrip
import com.starters.yeogida.databinding.FragmentTripSearchBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripSearchFragment : Fragment() {
    private lateinit var binding: FragmentTripSearchBinding
    private val searchService = YeogidaClient.searchService

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

        binding.tbTripSearch.setNavigationOnClickListener {
            requireActivity().finish()
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
                moveToSearchResult(searchKeyword)
                addRecentChip(searchKeyword)
            })
    }

    private fun moveToSearchResult(searchKeyword: String) {
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

    private fun setRankAdapter(rankList: List<RankTrip>) {
        with(binding.rvSearchTrip) {
            adapter = PopularSearchAdapter(rankList, viewModel)
        }
    }

    private fun initRecentChip() {
        with(binding.chipGroupRecent) {
            // TODO. Room 에 있는 값 가져오기
            addView(
                Chip(requireContext(), null, R.attr.recentSearchChipStyle).apply {
                    text = "test"
                }
            )
        }
    }

    private fun addRecentChip(searchKeyword: String) {
        with(binding.chipGroupRecent) {
            // TODO. 최대 10개만 가지고 있기, 10개 이상일때는 제일 오래된 거 지우고 넣기
            addView(
                Chip(requireContext(), null, R.attr.recentSearchChipStyle).apply {
                    text = searchKeyword
                }
            )
        }
    }
}