package com.starters.yeogida.presentation.like

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.local.LikeTripData
import com.starters.yeogida.data.remote.response.trip.LikeTrip
import com.starters.yeogida.databinding.FragmentLikeTripBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.common.RegionCategory
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LikeTripFragment : Fragment() {
    private lateinit var binding: FragmentLikeTripBinding
    private val viewModel: LikeTripViewModel by viewModels()
    private val dataStore = YeogidaApplication.getInstance().getDataStore()
    private val tripService = YeogidaClient.tripService

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

        val choice = arguments?.getInt(REGION_CATEGORY_ITEM)
        initAdapter(choice)

        setTripClickListener()
    }

    private fun initRegionLikeTrip(region: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.getRegionLikeTrip(
                dataStore.userBearerToken.first(),
                region
            )

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let { data ->
                        val list = data.tripList

                        with(LikeTripLists) {
                            when (region) {
                                "서울" -> fetchList(seoul, list)
                                "경기도" -> fetchList(gyeonggi, list)
                                "인천" -> fetchList(incheon, list)
                                "세종" -> fetchList(sejong, list)
                                "강원도" -> fetchList(kangwon, list)
                                "충청북도" -> fetchList(chungbuk, list)
                                "충청남도" -> fetchList(chungnam, list)
                                "대전" -> fetchList(daejeon, list)
                                "광주" -> fetchList(kwangju, list)
                                "전라북도" -> fetchList(jeonbuk, list)
                                "전라남도" -> fetchList(jeonnam, list)
                                "대구" -> fetchList(daegu, list)
                                "울산" -> fetchList(ulsan, list)
                                "부산" -> fetchList(busan, list)
                                "경상북도" -> fetchList(kyeongbuk, list)
                                "경상남도" -> fetchList(kyeongnam, list)
                                else -> fetchList(jeju, list)
                            }
                        }

                        withContext(Dispatchers.Main) {
                            binding.rvTrip.adapter?.notifyDataSetChanged()
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun fetchList(
        regionList: MutableList<LikeTripData>,
        list: List<LikeTrip>
    ) {
        regionList.clear()

        for (i in list.indices) {
            regionList.add(
                LikeTripData(
                    list[i].tripId,
                    RegionCategory.ALL,
                    list[i].imgUrl,
                    list[i].title,
                    list[i].subTitle,
                    list[i].nickname,
                    true,
                    list[i].heartCount,
                    list[i].placeCount
                )
            )
        }
    }


    private fun initLikeTrip() {

        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.getLikeTrip(
                dataStore.userBearerToken.first()
            )

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let { data ->
                        val list = data.tripList

                        LikeTripLists.all.clear()

                        for (i in list.indices) {
                            LikeTripLists.all.add(
                                LikeTripData(
                                    list[i].tripId,
                                    RegionCategory.ALL,
                                    list[i].imgUrl,
                                    list[i].title,
                                    list[i].subTitle,
                                    list[i].nickname,
                                    true,
                                    list[i].heartCount,
                                    list[i].placeCount
                                )
                            )
                        }
                        withContext(Dispatchers.Main) {
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
            when (choice) {
                0 -> {
                    setLikeAdapter(LikeTripLists.all)
                    initLikeTrip()
                }
                1 -> {
                    setLikeAdapter(LikeTripLists.seoul)
                    initRegionLikeTrip("서울")
                }
                2 -> {
                    setLikeAdapter(LikeTripLists.gyeonggi)
                    initRegionLikeTrip("경기도")
                }
                3 -> {
                    setLikeAdapter(LikeTripLists.incheon)
                    initRegionLikeTrip("인천")
                }
                4 -> {
                    setLikeAdapter(LikeTripLists.sejong)
                    initRegionLikeTrip("세종")
                }
                5 -> {
                    setLikeAdapter(LikeTripLists.kangwon)
                    initRegionLikeTrip("강원도")
                }
                6 -> {
                    setLikeAdapter(LikeTripLists.chungbuk)
                    initRegionLikeTrip("충청북도")
                }
                7 -> {
                    setLikeAdapter(LikeTripLists.chungnam)
                    initRegionLikeTrip("충청남도")
                }
                8 -> {
                    setLikeAdapter(LikeTripLists.daejeon)
                    initRegionLikeTrip("대전")
                }
                9 -> {
                    setLikeAdapter(LikeTripLists.kwangju)
                    initRegionLikeTrip("광주")
                }
                10 -> {
                    setLikeAdapter(LikeTripLists.jeonbuk)
                    initRegionLikeTrip("전라북도")
                }
                11 -> {
                    setLikeAdapter(LikeTripLists.jeonnam)
                    initRegionLikeTrip("전라남도")
                }
                12 -> {
                    setLikeAdapter(LikeTripLists.daegu)
                    initRegionLikeTrip("대구")
                }
                13 -> {
                    setLikeAdapter(LikeTripLists.ulsan)
                    initRegionLikeTrip("울산")
                }
                14 -> {
                    setLikeAdapter(LikeTripLists.busan)
                    initRegionLikeTrip("부산")
                }
                15 -> {
                    setLikeAdapter(LikeTripLists.kyeongbuk)
                    initRegionLikeTrip("경상북도")
                }
                16 -> {
                    setLikeAdapter(LikeTripLists.kyeongnam)
                    initRegionLikeTrip("경상남도")
                }
                17 -> {
                    setLikeAdapter(LikeTripLists.jeju)
                    initRegionLikeTrip("제주")
                }
                else -> {}
            }
        }
    }

    private fun moveToAroundPlace(tripId: Long) {
        val intent = Intent(requireContext(), PlaceActivity::class.java)
        intent.putExtra("tripId", tripId)
        startActivity(intent)
    }

    private fun setTripClickListener() {
        viewModel.openAroundPlaceEvent.observe(viewLifecycleOwner, EventObserver { tripId ->
            Log.e("tripId", tripId.toString())
            moveToAroundPlace(tripId)
        })
    }

    private fun setLikeAdapter(list: List<LikeTripData>) {
        with(binding.rvTrip) {
            adapter = LikeTripAdapter(list, viewModel).apply {
                itemClick = object : LikeTripAdapter.ItemClick {
                    override fun onClick(likeBtn: View, likeTrip: LikeTripData) {

                        if (likeBtn.isSelected) { // 좋아요가 눌려있을 때
                            showDeleteLikeDialog(likeTrip, likeBtn)
                        } else { // 좋아요가 눌려있지 않을 때
                            addLikeTrip(likeTrip, likeBtn)
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteLikeDialog(
        likeTrip: LikeTripData,
        likeBtn: View
    ) {
        CustomDialog(requireContext()).apply {
            showDialog()
            setTitle("좋아요를 취소하시겠습니까?")

            setPositiveBtn("확인") {
                deleteLikeTrip(likeTrip, likeBtn)
                dismissDialog()
            }
            setNegativeBtn("닫기") {
                dismissDialog()
            }
        }
    }

    private fun addLikeTrip(
        likeTrip: LikeTripData,
        likeBtn: View
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.postTripHeart(
                dataStore.userBearerToken.first(),
                likeTrip.tripId
            )

            when (response.code()) {
                201 -> {
                    withContext(Dispatchers.Main) {
                        likeBtn.isSelected = true
                    }
                }
                else -> {
                    Log.e("LikeResponse/Error", response.message())
                    withContext(Dispatchers.Main) {
                        requireContext().shortToast("에러")
                    }
                }
            }
        }
    }

    private fun deleteLikeTrip(
        likeTrip: LikeTripData,
        likeBtn: View
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.deleteTripHeart(
                dataStore.userBearerToken.first(),
                likeTrip.tripId
            )

            when (response.code()) {
                200 -> {
                    withContext(Dispatchers.Main) {
                        likeBtn.isSelected = false
                        requireContext().shortToast("좋아요를 취소하였습니다")
                    }
                }
                else -> {
                    Log.e("LikeResponse/Error", response.message())
                    withContext(Dispatchers.Main) {
                        requireContext().shortToast("에러")
                    }
                }
            }
        }
    }
}