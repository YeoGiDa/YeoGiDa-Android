package com.starters.yeogida.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.local.SearchKeyword
import com.starters.yeogida.data.remote.response.trip.SearchTrip
import com.starters.yeogida.databinding.FragmentTripSearchResultBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.around.TripSortBottomSheetFragment
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

class TripSearchResultFragment : Fragment() {
    private lateinit var binding: FragmentTripSearchResultBinding
    private val searchService = YeogidaClient.searchService

    private val viewModel: SearchViewModel by viewModels()
    private val dataBase = YeogidaApplication.getInstance().getDataBase()

    private var sortValue: String = "id"
    private var tripList = listOf<SearchTrip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTripSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnBackPressed()
        setTripAdapter()
        setSearchListener()

        setOnTripClicked()
        initSearchResult()
        initBottomSheet()
    }

    // TODO. 검색 결과 엠티뷰 나중에 할 때 사용하기
    private fun initTripListView() {
        if (tripList.isEmpty()) {

        } else {

        }
    }

    private fun softKeyboardHide() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun getSortedData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = searchService.getSearchResult(
                binding.etTripSearch.text.toString(),
                sortValue
            )

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let { data ->
                        tripList = data.tripList
                        withContext(Dispatchers.Main) {
                            when (sortValue) {
                                "id" -> binding.btnTripSearchResultSort.text = "최신순"
                                "heart" -> binding.btnTripSearchResultSort.text = "인기순"
                            }
                            initTripListView()
                            setVisibility()
                            setTripAdapter()
                            binding.rvTrip.adapter?.notifyDataSetChanged()
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun initBottomSheet() {
        binding.btnTripSearchResultSort.setOnClickListener {
            val bottomSheetDialog = TripSortBottomSheetFragment {
                binding.btnTripSearchResultSort.text = it
                when (it) {
                    "최신순" -> sortValue = "id"
                    "인기순" -> sortValue = "heart"
                }
                if (!binding.etTripSearch.text.isNullOrBlank()) {
                    getSortedData()
                }
            }
            bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        }
    }

    private fun setOnTripClicked() {
        viewModel.openAroundPlaceEvent.observe(
            viewLifecycleOwner,
            EventObserver { tripId ->
                Intent(requireContext(), PlaceActivity::class.java).apply {
                    putExtra("tripId", tripId)
                    startActivity(this)
                }
            }
        )
    }

    private fun setSearchListener() {
        with(binding) {
            etTripSearch.setOnEditorActionListener { textView, action, keyEvent ->
                var handled = false
                val searchKeyword = textView.text.toString()

                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    if (!searchKeyword.isNullOrBlank()) {
                        addRecentChip(textView.text.toString())
                        getSortedData()
                        sortValue = "id"
                        binding.btnTripSearchResultSort.text = "최신순"
                        softKeyboardHide()
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
                    addRecentChip(binding.etTripSearch.text.toString())
                    sortValue = "id"
                    binding.btnTripSearchResultSort.text = "최신순"
                    getSortedData()
                    softKeyboardHide()
                } else {
                    requireActivity().shortToast("검색어를 입력해주세요")
                }
            }
        }
    }

    private fun setTripAdapter() {
        binding.rvTrip.adapter = SearchTripAdapter(tripList, viewModel)
    }

    private fun setOnBackPressed() {
        binding.tbTripSearch.setNavigationOnClickListener {
            findNavController().navigateUp()
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

    private fun initSearchResult() {
        val searchKeyword = requireArguments().getString("searchKeyword").toString()
        binding.etTripSearch.setText(searchKeyword)

        CoroutineScope(Dispatchers.IO).launch {
            val response = searchService.getSearchResult(
                searchKeyword,
                sortValue
            )

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data

                    data?.let { data ->
                        tripList = data.tripList

                        withContext(Dispatchers.Main) {
                            setTripAdapter()
                            binding.btnTripSearchResultSort.text = "최신순"
                            binding.rvTrip.adapter?.notifyDataSetChanged()
                            setVisibility()
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    private fun setVisibility() {
        if (tripList.isEmpty()) {
            binding.layoutTripSearchResultTop.visibility = View.GONE
            binding.btnTripSearchResultSort.visibility = View.GONE
            binding.rvTrip.visibility = View.GONE
            binding.tvResultEmpty.visibility = View.VISIBLE
        } else {
            binding.layoutTripSearchResultTop.visibility = View.VISIBLE
            binding.btnTripSearchResultSort.visibility = View.VISIBLE
            binding.rvTrip.visibility = View.VISIBLE
            binding.tvResultEmpty.visibility = View.GONE
        }
    }

    fun onMoveTopClicked(v: View) {
        binding.svTripSearchResult.smoothScrollTo(0, 0)
    }
}
