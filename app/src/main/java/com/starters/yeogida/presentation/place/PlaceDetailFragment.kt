package com.starters.yeogida.presentation.place

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.starters.yeogida.data.local.CommentData
import com.starters.yeogida.data.local.PlaceDetailData
import com.starters.yeogida.databinding.FragmentPlaceDetailBinding

class PlaceDetailFragment : Fragment() {
    private lateinit var binding: FragmentPlaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.place = PlaceDetailData(
            "단양 여기는 어디인가",
            "충청북도 단양군 단양읍 어쩌구 저쩌구 123-456",
            "관광지",
            3.5F,
            "웅진씽크빅\n유데미 스타터스\n화이팅"
        )

        val commentsList = mutableListOf<CommentData>().apply {
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2018/02/17/13/08/the-body-of-water-3159920__480.jpg",
                    "user1",
                    "22.12.13",
                    "댓글\n내용1"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2018/02/03/05/30/dodam-sambong-3126970__480.jpg",
                    "user2",
                    "22.12.14",
                    "댓글\n" +
                            "내용2"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2019/11/01/06/00/republic-of-korea-4593403__480.jpg",
                    "user3",
                    "22.12.15",
                    "댓글\n" +
                            "내용3"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2018/02/17/13/08/the-body-of-water-3159920__480.jpg",
                    "user1",
                    "22.12.13",
                    "댓글\n내용1"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2018/02/03/05/30/dodam-sambong-3126970__480.jpg",
                    "user2",
                    "22.12.14",
                    "댓글\n" +
                            "내용2"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2019/11/01/06/00/republic-of-korea-4593403__480.jpg",
                    "user3",
                    "22.12.15",
                    "댓글\n" +
                            "내용3"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2018/02/17/13/08/the-body-of-water-3159920__480.jpg",
                    "user1",
                    "22.12.13",
                    "댓글\n내용1"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2018/02/03/05/30/dodam-sambong-3126970__480.jpg",
                    "user2",
                    "22.12.14",
                    "댓글\n" +
                            "내용2"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2019/11/01/06/00/republic-of-korea-4593403__480.jpg",
                    "user3",
                    "22.12.15",
                    "댓글\n" +
                            "내용3"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2018/02/17/13/08/the-body-of-water-3159920__480.jpg",
                    "user1",
                    "22.12.13",
                    "댓글\n내용1"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2018/02/03/05/30/dodam-sambong-3126970__480.jpg",
                    "user2",
                    "22.12.14",
                    "댓글\n" +
                            "내용2"
                )
            )
            add(
                CommentData(
                    "https://cdn.pixabay.com/photo/2019/11/01/06/00/republic-of-korea-4593403__480.jpg",
                    "user3",
                    "22.12.15",
                    "댓글\n" +
                            "내용3"
                )
            )
        }

        binding.tvCommentCount.text = "댓글\t ${commentsList.size}"

        with(binding.rvPlaceDetailComment) {
            adapter = CommentAdapter(commentsList)
            // Divider 추가
            addItemDecoration(DividerItemDecoration(requireContext(), HORIZONTAL))
        }

        val images = mutableListOf<String>().apply {
            add("https://cdn.pixabay.com/photo/2018/02/17/13/08/the-body-of-water-3159920__480.jpg")
            add("https://cdn.pixabay.com/photo/2018/02/03/05/30/dodam-sambong-3126970__480.jpg")
            add("https://cdn.pixabay.com/photo/2019/11/01/06/00/republic-of-korea-4593403__480.jpg")
            add("https://cdn.pixabay.com/photo/2019/11/01/06/00/republic-of-korea-4593403__480.jpg")
            add("https://cdn.pixabay.com/photo/2018/08/23/17/27/paragliding-3626288__480.jpg")
        }
        with(binding.viewpagerPlaceToolbar) {
            adapter = PlaceDetailPhotoAdapter(requireContext(), images)
            binding.indicatorPlaceDetailToolbar.attachToPager(this)
        }
    }
}