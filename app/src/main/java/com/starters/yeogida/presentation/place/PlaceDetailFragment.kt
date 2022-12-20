package com.starters.yeogida.presentation.place

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.request.place.CommentRequest
import com.starters.yeogida.data.remote.response.CommentData
import com.starters.yeogida.data.remote.response.place.PlaceImg
import com.starters.yeogida.databinding.FragmentPlaceDetailBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.util.customEnqueue
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class PlaceDetailFragment : Fragment() {
    private lateinit var binding: FragmentPlaceDetailBinding
    private val dataStore = YeogidaApplication.getInstance().getDataStore()
    private lateinit var token: String
    private var memberId by Delegates.notNull<Long>()
    private var placeId: Long = 0

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

        initAuthorization()
        initPlaceData()
        checkActiveAndLength()
    }

    private fun initAuthorization() {
        CoroutineScope(Dispatchers.IO).launch {
            token = dataStore.userBearerToken.first()
            memberId = dataStore.memberId.first()
        }
    }

    private fun initPlaceData() {
        placeId = requireArguments().getLong("placeId")
        Log.e("PlaceDetail/placeId", placeId.toString())
        getPlaceDetail()
        initCommentNetwork()
    }

    private fun getPlaceDetail() {
        val placeService = YeogidaClient.placeService
        CoroutineScope(Dispatchers.IO).launch {
            val response = placeService.getPlaceDetail(placeId)

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let {
                        withContext(Dispatchers.Main) {
                            binding.place = data
                            setToolbar(data.placeImgs)
                        }
                    }
                }
            }
        }
    }

    private fun setToolbar(placeImages: List<PlaceImg>) {

        with(binding.viewpagerPlaceToolbar) {
            adapter = PlaceDetailPhotoAdapter(placeImages)
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

    // 댓글 조회 api
    private fun initCommentNetwork() {
        YeogidaClient.placeService.getAscComments(
            placeId
        ).customEnqueue(
            onSuccess = { responseData ->
                if (responseData.code == 200) {
                    val commentsList = mutableListOf<CommentData>().apply {
                        responseData.data?.let { data -> addAll(data.commentList) }
                    }

                    with(binding.rvPlaceDetailComment) {
                        adapter = CommentAdapter(
                            commentsList,
                            memberId
                        ) { commentAttribute: String, commentId: Long ->
                            initAttributeDialog(commentAttribute, commentId)
                        }
                    }
                    binding.tvCommentCount.text = "댓글 ${responseData.data?.commentCounts}"
                }
            }
        )
    }

    // 속성에 따른 다이알로그
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

    // 댓글 삭제 api
    private fun initDeleteCommentNetwork(commentId: Long) {
        YeogidaClient.placeService.deleteComment(
            token,
            commentId
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

    private fun softKeyboardHide() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    // 댓글 추가 api
    fun sendComment(view: View) {
        val commentRequest = CommentRequest(
            binding.etPlaceDetailComment.text.toString()
        )

        YeogidaClient.placeService.postComment(
            token,
            placeId,
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
