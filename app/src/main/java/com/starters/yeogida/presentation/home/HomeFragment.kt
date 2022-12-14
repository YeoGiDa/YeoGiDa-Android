package com.starters.yeogida.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.starters.yeogida.R
import com.starters.yeogida.data.local.BestTravelerData
import com.starters.yeogida.databinding.FragmentHomeBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.mypage.MyPageActivity
import com.starters.yeogida.presentation.trip.AddTripActivity
import com.starters.yeogida.util.customEnqueue

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.view = this
        initAdapter()
        initNetwork()
    }

    private fun initAdapter() {
        val bestTravelerAdapter = BestTravelerAdapter()
        val bestTripAdapter = TripAdapter()
        val recentTripAdapter = TripAdapter()

        binding.rvBestTraveler.adapter = bestTravelerAdapter
        binding.rvBestMonthlyTrip.adapter = bestTripAdapter
        binding.rvRecentTrip.adapter = recentTripAdapter

        bestTravelerAdapter.bestTravelerList.addAll(
            listOf(
                BestTravelerData(
                    "https://image.bugsm.co.kr/album/images/500/204708/20470857.jpg",
                    "하현상",
                    "999+"
                ),
                BestTravelerData("https://image.yes24.com/goods/96827725/XL", "호피폴라", "129"),
                BestTravelerData(
                    "https://cdn.pixabay.com/photo/2017/08/06/12/06/people-2591874_1280.jpg",
                    "여행왕",
                    "20"
                ),
                BestTravelerData(
                    "https://cdn.pixabay.com/photo/2017/06/05/11/01/airport-2373727_1280.jpg",
                    "뱅기",
                    "14"
                ),
                BestTravelerData(
                    "https://cdn.pixabay.com/photo/2016/11/22/22/21/adventure-1850912_1280.jpg",
                    "레모나",
                    "2"
                )
            )
        )
        bestTravelerAdapter.notifyDataSetChanged()
    }

    private fun initNetwork() {
        val bestTripAdapter = TripAdapter()
        binding.rvBestMonthlyTrip.adapter = bestTripAdapter

        YeogidaClient.homeService.getMonthlyBest().customEnqueue(
            onSuccess = {
                it.data?.let { it1 -> bestTripAdapter.tripList.addAll(it1.tripList) }
                bestTripAdapter.notifyDataSetChanged()
            }
        )
    }

    fun moveToMyPage(view: View) {
        val intent = Intent(activity, MyPageActivity::class.java)
        startActivity(intent)
    }

    fun moveToAddTrip(view: View) {
        val intent = Intent(activity, AddTripActivity::class.java)
        startActivity(intent)
    }
}
