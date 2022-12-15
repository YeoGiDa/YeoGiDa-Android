package com.starters.yeogida.presentation.place

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.local.PlaceDetailData
import com.starters.yeogida.data.remote.request.place.CommentRequest
import com.starters.yeogida.data.remote.response.CommentData
import com.starters.yeogida.databinding.FragmentPlaceDetailBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.util.customEnqueue
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaceDetailFragment : Fragment() {
    private lateinit var binding: FragmentPlaceDetailBinding
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

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

        binding.view = this
        binding.btnCommentSubmit.isEnabled = false
        initPlaceData()
        setToolbar()
        initCommentNetwork()
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

    private fun initCommentNetwork() {
        CoroutineScope(Dispatchers.IO).launch {
            val memberId: Long = dataStore.memberId.first()

            YeogidaClient.placeService.getAscComments(
                2
            ).customEnqueue(
                onSuccess = { responseData ->
                    if (responseData.code == 200) {
                        val commentsList = mutableListOf<CommentData>().apply {
                            responseData.data?.let { it1 -> addAll(it1.commentList) }
                        }

                        with(binding.rvPlaceDetailComment) {
                            adapter = CommentAdapter(commentsList, memberId) { commentAttribute: String, commentId: Long ->
                                initAttributeDialog(commentAttribute, commentId)
                            }
                        }
                        binding.tvCommentCount.text = "댓글\t ${responseData.data?.commentCounts}"
                    }
                }
            )
        }
    }

    private fun initAttributeDialog(commentAttribute: String, commentId: Long) {
        when (commentAttribute) {
            "삭제" -> initDeleteDialog(commentId)
            "신고" -> initReportDialog(commentId)
        }
    }

    private fun initDeleteDialog(commentId: Long) {
        setCustomDialog("정말 삭제하시겠습니까?", "삭제", commentId)
    }

    private fun initReportDialog(commentId: Long) {
        setCustomDialog("정말 신고하시겠습니까?", "신고", commentId)
    }

    private fun setCustomDialog(title: String, positive: String, commentId: Long) {
        CustomDialog(requireContext()).apply {
            showDialog()
            setTitle(title)
            setPositiveBtn(positive) {
                when (positive) {
                    "삭제" -> {
                        initDeleteCommentNetwork(commentId)
                        dismissDialog()
                    }
                }
            }
            setNegativeBtn("취소") {
                dismissDialog()
            }
        }
    }

    private fun initDeleteCommentNetwork(l: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            YeogidaClient.placeService.deleteComment(
                dataStore.userBearerToken.first(),
                l
            ).customEnqueue(
                onSuccess = {
                    if (it.code == 200) {
                        requireContext().shortToast("댓글을 삭제했습니다.")
                        initCommentNetwork()
                    } else {
                        requireContext().shortToast("댓글 삭제에 실패했습니다.")
                    }
                }
            )
        }
    }

    private fun softKeyboardHide() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    fun sendComment(view: View) {
        val commentRequest = CommentRequest(
            binding.etPlaceDetailComment.text.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            YeogidaClient.placeService.postComment(
                dataStore.userBearerToken.first(),
                2,
                commentRequest
            ).customEnqueue(
                onSuccess = {
                    if (it.code == 201) {
                        initCommentNetwork()
                        binding.etPlaceDetailComment.text.clear()
                        softKeyboardHide()
                    }
                }
            )
        }
    }
}
