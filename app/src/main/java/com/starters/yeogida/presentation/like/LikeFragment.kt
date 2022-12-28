package com.starters.yeogida.presentation.like

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentLikeBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.like.LikeTripObj.regionList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LikeFragment : Fragment() {
    private lateinit var binding: FragmentLikeBinding
    private var regionCount = 0

    private val tripService = YeogidaClient.tripService
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private val viewModel: LikeTripViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        regionList.clear()
        getRegionData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        regionList.clear()
        getRegionData()
    }

    private fun getRegionData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.getLikeTrip(
                dataStore.userBearerToken.first()
            )

            Log.e("responseCode", response.code().toString())
            when (response.code()) {
                200 -> {
                    withContext(Dispatchers.Main) {
                        binding.appbarLike.visibility = View.VISIBLE
                        binding.viewpagerLike.visibility = View.VISIBLE
                        binding.layoutLikeTripEmpty.visibility = View.GONE // 엠티뷰 숨기기
                    }
                    val tripList = response.body()!!.data!!.tripList

                    if (!regionList.contains("전체")) regionList.add("전체")

                    for (i in tripList.indices) {
                        Log.e("getRegionData", regionList.toString())
                        Log.e("getRegionData", tripList[i].region)
                        if (regionList.contains(tripList[i].region)) continue
                        regionList.add(tripList[i].region)
                    }

                    regionCount = regionList.size    // 어떤 유저의 좋아요 한 여행지들의 지역 수

                    withContext(Dispatchers.Main) {
                        binding.viewpagerLike.apply {
                            adapter = LikePagerFragmentStateAdapter(
                                context as FragmentActivity,
                                regionCount
                            )
                        }

                        TabLayoutMediator(binding.tabLike, binding.viewpagerLike) { tab, position ->
                            tab.text = regionList[position]
                        }.attach()
                    }
                }
                404 -> {
                    // 좋아요한 여행지가 없을 때
                    withContext(Dispatchers.Main) {
                        binding.appbarLike.visibility = View.GONE   // 상단 앱 바
                        binding.viewpagerLike.visibility = View.GONE    // 뷰 페이저 숨기기
                        binding.layoutLikeTripEmpty.visibility = View.VISIBLE // 엠티뷰 보이기
                    }
                }
                else -> {

                }
            }
        }
    }
}