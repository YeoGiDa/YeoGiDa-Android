package com.starters.yeogida.presentation.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.data.remote.response.place.PlaceData
import com.starters.yeogida.databinding.FragmentMyCommentPlaceBinding
import com.starters.yeogida.presentation.around.AroundPlaceAdapter
import com.starters.yeogida.presentation.around.AroundPlaceViewModel

class MyCommentPlaceFragment : Fragment() {
    private lateinit var binding: FragmentMyCommentPlaceBinding
    private val viewModel: AroundPlaceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCommentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.view = this
        initClickListener()
        initData()
    }

    private fun initData() {
        val aroundPlaceAdapter = AroundPlaceAdapter(viewModel)
        binding.rvCommentPlace.adapter = aroundPlaceAdapter
        aroundPlaceAdapter.aroundPlaceList.addAll(
            listOf(
                PlaceData(
                    1,
                    "눈사람",
                    3F,
                    "https://cdn.pixabay.com/photo/2022/01/30/17/16/snowman-6981771_1280.jpg",
                    3,
                    "카페"
                ),
                PlaceData(
                    2,
                    "타이틀이요",
                    4F,
                    "https://cdn.pixabay.com/photo/2013/04/11/19/46/building-102840_1280.jpg",
                    12,
                    "카페"
                ),
                PlaceData(
                    3,
                    "김밥말아",
                    2F,
                    "https://cdn.pixabay.com/photo/2017/08/07/23/11/iceland-2608985_1280.jpg",
                    0,
                    "바"
                ),
                PlaceData(
                    4,
                    "김밥맛집",
                    1F,
                    "https://cdn.pixabay.com/photo/2021/12/17/09/34/christmas-drink-6876097_1280.jpg",
                    2,
                    "쇼핑"
                ),
                PlaceData(
                    5,
                    "곰돌이박물관",
                    5F,
                    "https://cdn.pixabay.com/photo/2013/04/11/19/46/building-102840_1280.jpg",
                    31,
                    "숙소"
                ),
                PlaceData(
                    6,
                    "코틀린",
                    4F,
                    "https://cdn.pixabay.com/photo/2022/11/09/21/12/candle-7581472_1280.jpg",
                    3,
                    "기타"
                ),
                PlaceData(
                    7,
                    "연어맛집",
                    4F,
                    "https://cdn.pixabay.com/photo/2022/11/21/14/28/church-7607172_640.jpg",
                    13,
                    "카페"
                )
            )
        )
        aroundPlaceAdapter.notifyDataSetChanged()
    }

    private fun initClickListener() {
        binding.tbCommentPlace.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun moveToTop(view: View) {
        binding.scrollViewCommentPlace.smoothScrollTo(0, 0)
    }
}
