package com.starters.yeogida.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.R
import com.starters.yeogida.data.local.BestTravelerData
import com.starters.yeogida.data.local.TripData
import com.starters.yeogida.databinding.FragmentHomeBinding

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

        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_mypage)
        }
        initAdapter()
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
                BestTravelerData("https://image.bugsm.co.kr/album/images/500/204708/20470857.jpg", "하현상", "999+"),
                BestTravelerData("https://image.yes24.com/goods/96827725/XL", "호피폴라", "129"),
                BestTravelerData("https://cdn.pixabay.com/photo/2017/08/06/12/06/people-2591874_1280.jpg", "여행왕", "20"),
                BestTravelerData("https://cdn.pixabay.com/photo/2017/06/05/11/01/airport-2373727_1280.jpg", "뱅기", "14"),
                BestTravelerData("https://cdn.pixabay.com/photo/2016/11/22/22/21/adventure-1850912_1280.jpg", "레모나", "2")
            )
        )
        bestTripAdapter.tripList.addAll(
            listOf(
                TripData("https://cdn.pixabay.com/photo/2017/08/31/09/26/korea-2699930_1280.jpg", "담양", "설렁탕탕구리"),
                TripData("https://cdn.pixabay.com/photo/2020/05/05/07/52/republic-of-korea-5131925_1280.jpg", "제주도", "닉넴"),
                TripData("https://cdn.pixabay.com/photo/2022/08/05/05/59/korea-7366036_1280.jpg", "서울", "텀플러러버"),
                TripData("https://cdn.pixabay.com/photo/2020/11/24/02/13/gyeongbok-palace-5771324_1280.jpg", "서울", "한복좋아"),
                TripData("https://cdn.pixabay.com/photo/2016/08/23/09/38/pohang-1613923_1280.jpg", "포항", "포항항")
            )
        )
        recentTripAdapter.tripList.addAll(
            listOf(
                TripData("https://cdn.pixabay.com/photo/2016/08/19/04/59/the-bulguksa-temple-1604556_1280.jpg", "경주", "귤귤"),
                TripData("https://cdn.pixabay.com/photo/2020/05/24/11/56/to-5213925_1280.jpg", "경주", "포항항"),
                TripData("https://cdn.pixabay.com/photo/2020/03/19/07/37/suwon-4946628_1280.jpg", "수원", "비타민C"),
                TripData("https://cdn.pixabay.com/photo/2017/04/03/11/14/gangneung-2198025_1280.jpg", "강릉", "지브리짱")
            )
        )

        bestTravelerAdapter.notifyDataSetChanged()
        bestTripAdapter.notifyDataSetChanged()
        recentTripAdapter.notifyDataSetChanged()
    }
}
