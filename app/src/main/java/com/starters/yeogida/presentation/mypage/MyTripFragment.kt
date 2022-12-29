package com.starters.yeogida.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        return binding.root
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
            val response = myPageService.getMyTrip(
                dataStore.userBearerToken.first()
            )

            when (response.code()) {
                200 -> {
                    response.body()?.data?.let { data ->
                        tripList = data.tripList

                        Log.e("tripList", tripList.toString())

                        withContext(Dispatchers.Main) {
                            initAdapter()
                            binding.rvMyTrip.adapter?.notifyDataSetChanged()
                        }
                    }
                }
                else -> {}
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
