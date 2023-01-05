package com.starters.yeogida.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.response.mypage.MyTrip
import com.starters.yeogida.databinding.FragmentMyTripBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.presentation.trip.AddTripActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class MyTripFragment : Fragment() {
    private lateinit var binding: FragmentMyTripBinding
    private val viewModel: MyPageViewModel by viewModels()
    private val myPageService = YeogidaClient.myPageService
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private var tripList = listOf<MyTrip>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyTripBinding.inflate(inflater, container, false)
        binding.view = this

        setOnBackPressed()
        initTripClickListener()
        getMyTripData()

        initSearchListener()



        return binding.root
    }

    private fun initSearchListener() {
        binding.etSearchMyPlace.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = binding.etSearchMyPlace.text.toString()

                if (isValidSearchText(searchText)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = myPageService.searchMyTrip(
                            searchText
                        )

                        when (response.code()) {
                            200 -> {
                                response.body()?.data?.let { data ->
                                    tripList = data.tripList
                                }

                                withContext(Dispatchers.Main) {
                                    initAdapter()
                                    initTopButtonView()
                                    binding.rvMyTrip.adapter?.notifyDataSetChanged()
                                }
                            }

                            else -> {

                            }
                        }
                    }
                } else if (searchText == "") {
                    getMyTripData()
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

    private fun initTripClickListener() {
        viewModel.openAroundPlaceEvent.observe(viewLifecycleOwner, EventObserver { tripId ->
            Intent(requireContext(), PlaceActivity::class.java).apply {
                putExtra("tripId", tripId)
                startActivity(this)
            }
        })
    }


    override fun onResume() {
        super.onResume()
        getMyTripData()
    }


    private fun getMyTripData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = myPageService.getMyTrip()

            when (response.code()) {
                200 -> {
                    response.body()?.data?.let { data ->
                        tripList = data.tripList

                    }
                }
                else -> {}
            }

            withContext(Dispatchers.Main) {
                initAdapter()
                initView()
                binding.rvMyTrip.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun initTopButtonView() {
        with(binding) {
            if (tripList.isEmpty()) {
                layoutMyTripTop.visibility = View.GONE
            } else {
                layoutMyTripTop.visibility = View.VISIBLE
            }
        }
    }

    private fun initView() {
        with(binding) {
            if (tripList.isEmpty()) {
                layoutMyTripEmpty.visibility = View.VISIBLE
                layoutMyTripTop.visibility = View.GONE
                etSearchMyPlace.visibility = View.GONE

            } else {
                layoutMyTripEmpty.visibility = View.GONE
                layoutMyTripTop.visibility = View.VISIBLE
                etSearchMyPlace.visibility = View.VISIBLE
            }
        }
    }


    private fun setOnBackPressed() {
        binding.tbMyPlace.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initAdapter() {
        val tripAdapter = MyTripAdapter(tripList, viewModel)
        binding.rvMyTrip.adapter = tripAdapter
    }

    fun moveToTop(view: View) {
        binding.scrollViewMyPlace.smoothScrollTo(0, 0)
    }

    fun moveToAddTrip(view: View) {
        val intent = Intent(activity, AddTripActivity::class.java)
        startActivity(intent)
    }
}
