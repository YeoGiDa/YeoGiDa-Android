package com.starters.yeogida.presentation.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.data.local.PlaceDetailData
import com.starters.yeogida.data.remote.response.CommentData
import com.starters.yeogida.databinding.FragmentPlaceDetailBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.common.OnItemClick
import com.starters.yeogida.util.customEnqueue
import com.starters.yeogida.util.shortToast
import java.util.*
import java.util.Collections.addAll

class PlaceDetailFragment : Fragment(), OnItemClick {
    private lateinit var binding: FragmentPlaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnCommentSubmit.isEnabled = false
        initPlaceData()
        setToolbar()
        initNetwork()
        checkActiveAndLength()
    }

    private fun initPlaceData() {
        binding.place = PlaceDetailData(
            "단양 여기는 어디인가",
            "충청북도 단양군 단양읍 어쩌구 저쩌구 123-456",
            "관광지",
            3.5F,
            "웅진씽크빅\n유데미 스타터스\n화이팅"
        )
    }

    private fun setToolbar() {
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

        // 뒤로가기 리스너
        binding.tbPlaceDetail.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initNetwork() {
        YeogidaClient.placeService.getAscComments(
            2
        ).customEnqueue(
            onSuccess = {
                val commentsList = mutableListOf<CommentData>().apply {
                    it.data?.let { it1 -> addAll(it1.commentList) }
                }
                with(binding.rvPlaceDetailComment) {
                    adapter = CommentAdapter(commentsList, this@PlaceDetailFragment)
                }
                binding.tvCommentCount.text = "댓글\t ${it.data?.commentCounts}"
            }
        )
    }

    // 보내기 버튼 활성화 및 최대 글자수 확인
    private fun checkActiveAndLength() {
        binding.etPlaceDetailComment.addTextChangedListener {
            activeConfirmButton()
            if (binding.etPlaceDetailComment.text.length == 200) {
                requireContext().shortToast("최대 200자까지 작성 가능합니다.")
            }
        }
    }

    private fun activeConfirmButton() {
        binding.btnCommentSubmit.isEnabled =
            !binding.etPlaceDetailComment.text.isNullOrEmpty() && binding.etPlaceDetailComment.text.trim()
            .isNotEmpty()
    }

    private fun initDeleteDialog() {
        setCustomDialog("정말 삭제하시겠습니까?", "삭제")
    }

    private fun initReportDialog() {
        setCustomDialog("정말 신고하시겠습니까?", "신고")
    }

    private fun setCustomDialog(title: String, positive: String) {
        CustomDialog(requireContext()).apply {
            showDialog()
            setTitle(title)
            setPositiveBtn(positive) {
                dismissDialog()
            }
            setNegativeBtn("취소") {
                dismissDialog()
            }
        }
    }

    override fun onClick(value: String) {
        when (value) {
            "삭제" -> initDeleteDialog()
            "신고" -> initReportDialog()
        }
    }
}
