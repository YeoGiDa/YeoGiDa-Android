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
import com.starters.yeogida.databinding.FragmentLikeTripBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.around.TripSortBottomSheetFragment
import com.starters.yeogida.presentation.common.CustomDialog
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
    private var tripId: Long = 0L

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

        initBottomSheet()   // 정렬 바텀시트
        initLikeTrip()

        viewModel.openAroundPlaceEvent.observe(viewLifecycleOwner) {
            Log.e("openAroundPlaceEvent", "Event Observed.")
            viewModel.tripId.value?.let { tripId -> moveToAroundPlace(tripId) }
        }
    }

    private fun initLikeTrip() {
        val tripService = YeogidaClient.tripService
        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.getLikeTrip(
                YeogidaApplication.getInstance().getDataStore().userBearerToken.first()
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
                0 -> setLikeAdapter(LikeTripLists.all)
                1 -> setLikeAdapter(LikeTripLists.seoul)
                2 -> setLikeAdapter(LikeTripLists.gyeonggi)
                3 -> setLikeAdapter(LikeTripLists.incheon)
                4 -> setLikeAdapter(LikeTripLists.sejong)
                5 -> setLikeAdapter(LikeTripLists.kangwon)
                6 -> setLikeAdapter(LikeTripLists.chungbuk)
                7 -> setLikeAdapter(LikeTripLists.chungnam)
                8 -> setLikeAdapter(LikeTripLists.daejeon)
                9 -> setLikeAdapter(LikeTripLists.kwangju)
                10 -> setLikeAdapter(LikeTripLists.jeonbuk)
                11 -> setLikeAdapter(LikeTripLists.jeonnam)
                12 -> setLikeAdapter(LikeTripLists.daegu)
                13 -> setLikeAdapter(LikeTripLists.ulsan)
                14 -> setLikeAdapter(LikeTripLists.busan)
                15 -> setLikeAdapter(LikeTripLists.kyeongbuk)
                16 -> setLikeAdapter(LikeTripLists.kyeongnam)
                17 -> setLikeAdapter(LikeTripLists.jeju)
                else -> {}
            }
        }
    }

    private fun initBottomSheet() {
        binding.btnLikeTripSort.text = "최신순"

        binding.btnLikeTripSort.setOnClickListener {
            val bottomSheetDialog = TripSortBottomSheetFragment {
                binding.btnLikeTripSort.text = it
            }
            bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        }
    }

    private fun moveToAroundPlace(tripId: Long) {
        val intent = Intent(requireContext(), PlaceActivity::class.java)
        intent.putExtra("tripId", tripId)
        startActivity(intent)
    }

    private fun showDeleteDialog(view: View) {
        CustomDialog(requireContext()).apply {
            showDialog()
            setTitle("좋아요를 취소하시겠습니까?")

            setPositiveBtn("확인") {
                view.isSelected = false
                // 좋아요 취소 API
                requireContext().shortToast("좋아요를 취소하였습니다")
                dismissDialog()
            }
            setNegativeBtn("닫기") {
                dismissDialog()
            }
        }
    }

    private fun setLikeAdapter(likeTripList: List<LikeTripData>) {
        with(binding.rvTrip) {
            adapter = LikeTripAdapter(likeTripList, viewModel).apply {
                itemClick = object : LikeTripAdapter.ItemClick {
                    override fun onClick(view: View, position: Int) {
                        if (view.isSelected) {
                            showDeleteDialog(view)
                        } else {
                            addLikeTrip(view)
                        }
                    }
                }
            }
        }
    }

    private fun addLikeTrip(view: View) {
        // 좋아요 추가 API
        view.isSelected = true
    }

}