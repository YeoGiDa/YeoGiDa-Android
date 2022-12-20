package com.starters.yeogida.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.data.local.LikeTripData
import com.starters.yeogida.databinding.FragmentMyTripBinding
import com.starters.yeogida.presentation.common.RegionCategory
import com.starters.yeogida.presentation.like.LikeTripAdapter
import com.starters.yeogida.presentation.like.LikeTripViewModel
import com.starters.yeogida.presentation.trip.AddTripActivity

class MyTripFragment : Fragment() {
    private lateinit var binding: FragmentMyTripBinding
    private val viewModel: LikeTripViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyTripBinding.inflate(inflater, container, false)
        binding.view = this

        initClickListener()
        initData()

        return binding.root
    }

    private fun initClickListener() {
        binding.tbMyPlace.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initData() {
        val tripAdapter = LikeTripAdapter(
            listOf(
                LikeTripData(
                    RegionCategory.SEOUL,
                    "https://cdn.pixabay.com/photo/2022/10/08/06/31/hungarian-parliament-building-7506436_640.jpg",
                    "여기는 어디",
                    "여기는 헝가리",
                    "호빵맨",
                    true,
                    3,
                    9
                ),
                LikeTripData(
                    RegionCategory.SEOUL,
                    "https://cdn.pixabay.com/photo/2022/12/12/19/14/paris-7651738_1280.jpg",
                    "여기는 어디",
                    "여기는 프랑스",
                    "세균맨",
                    false,
                    2,
                    4
                ),
                LikeTripData(
                    RegionCategory.SEOUL,
                    "https://cdn.pixabay.com/photo/2022/12/01/00/13/antique-7627999_1280.jpg",
                    "책벌레",
                    "책을 읽어요",
                    "식빵맨",
                    true,
                    0,
                    2
                ),
                LikeTripData(
                    RegionCategory.SEOUL,
                    "https://cdn.pixabay.com/photo/2022/12/11/16/54/tree-7649287__480.jpg",
                    "참새",
                    "짹짹",
                    "스프",
                    true,
                    13,
                    11
                ),
                LikeTripData(
                    RegionCategory.SEOUL,
                    "https://cdn.pixabay.com/photo/2022/01/13/07/06/house-6934544__480.jpg",
                    "한옥",
                    "눈이 내려요",
                    "고양이",
                    true,
                    2,
                    1
                ),
                LikeTripData(
                    RegionCategory.SEOUL,
                    "https://cdn.pixabay.com/photo/2022/10/08/06/31/hungarian-parliament-building-7506436_640.jpg",
                    "여기는 어디",
                    "여기는 헝가리",
                    "호빵맨",
                    true,
                    3,
                    2
                )
            ),
            viewModel
        )
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
