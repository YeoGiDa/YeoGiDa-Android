package com.starters.yeogida.presentation.like

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.starters.yeogida.data.remote.response.common.TripResponse
import com.starters.yeogida.databinding.FragmentLikeTripBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.place.PlaceActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class LikeTripFragment : Fragment() {
    private lateinit var binding: FragmentLikeTripBinding
    private val viewModel: LikeTripViewModel by viewModels()
    private val tripService = YeogidaClient.tripService
    private lateinit var mContext: Context

    private val tripList = mutableListOf<TripResponse>()

    companion object {
        val REGION_CATEGORY_ITEM = "region_category_item"

        @JvmStatic
        fun newInstance(position: Int) =
            LikeTripFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION_CATEGORY_ITEM, position)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikeTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setTripClickListener()
        setMoveTop()
    }

    override fun onResume() {
        super.onResume()
        val choice = arguments?.getInt(REGION_CATEGORY_ITEM)
        initAdapter(choice)
        initSearchView(choice)
        setSearchTextChangedListener(choice)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initSearchView(choice: Int?) {
        choice?.let {
            when (choice) {
                0 -> {
                    binding.etLikeTripSearch.visibility = View.VISIBLE
                }
                else -> {
                    binding.etLikeTripSearch.visibility = View.GONE
                }
            }
        }
    }

    private fun setSearchTextChangedListener(choice: Int?) {
        binding.etLikeTripSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = binding.etLikeTripSearch.text.toString()
                if (isValidSearchText(searchText)) {
                    if (choice == 0) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = tripService.searchLikeTrip(
                                searchText
                            )

                            when (response.code()) {
                                200 -> {
                                    val data = response.body()?.data
                                    data?.let { data ->
                                        tripList.clear()
                                        tripList.addAll(data.tripList)

                                        withContext(Dispatchers.Main) {
                                            initMoveTopView(tripList)
                                            setLikeAdapter(tripList)
                                            binding.rvTrip.adapter?.notifyDataSetChanged()
                                        }
                                    }
                                }
                                else -> {
                                    Log.e("searchFollow", response.toString())
                                }
                            }
                        }
                    }
                }

                if (searchText == "" && choice == 0) {
                    initLikeTrip()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun isValidSearchText(searchText: String): Boolean {
        val regex = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{1,10}\$"
        val pattern = Pattern.compile(regex)

        val matcher = pattern.matcher(searchText)

        return matcher.matches()
    }

    private fun initRegionLikeTrip(region: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.getRegionLikeTrip(
                region
            )

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let { data ->
                        tripList.clear()
                        tripList.addAll(data.tripList)

                        withContext(Dispatchers.Main) {
                            initMoveTopView(tripList)
                            setLikeAdapter(tripList)
                            binding.rvTrip.adapter?.notifyDataSetChanged()
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun initLikeTrip() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.getLikeTrip()

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let { data ->
                        tripList.clear()
                        tripList.addAll(data.tripList)

                        withContext(Dispatchers.Main) {
                            initMoveTopView(tripList)
                            setLikeAdapter(tripList)
                            binding.rvTrip.adapter?.notifyDataSetChanged()
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun initAdapter(choice: Int?) {
        choice?.let {
            if (choice == 0) {
                initLikeTrip()
            } else {
                Log.e("choice", choice.toString())
                Log.e("region", LikeTripObj.regionList[choice])
                initRegionLikeTrip(LikeTripObj.regionList[choice])
            }
        }
    }

    private fun moveToAroundPlace(tripId: Long) {
        val intent = Intent(mContext, PlaceActivity::class.java)
        intent.putExtra("tripId", tripId)
        startActivity(intent)
    }

    private fun setTripClickListener() {
        viewModel.openAroundPlaceEvent.observe(viewLifecycleOwner, EventObserver { tripId ->
            Log.e("tripId", tripId.toString())
            moveToAroundPlace(tripId)
        })
    }

    private fun setLikeAdapter(list: List<TripResponse>) {
        with(binding.rvTrip) {
            adapter = LikeTripAdapter(tripList, viewModel)
        }
    }

    private fun initMoveTopView(list: List<TripResponse>) {
        if (list.isEmpty()) {
            binding.layoutLikeTripTop.visibility = View.GONE
        } else {
            binding.layoutLikeTripTop.visibility = View.VISIBLE
        }
    }

    private fun setMoveTop() {
        viewModel.moveTopEvent.observe(viewLifecycleOwner) {
            binding.svLikeTrip.smoothScrollTo(0, 0)
        }
    }
}