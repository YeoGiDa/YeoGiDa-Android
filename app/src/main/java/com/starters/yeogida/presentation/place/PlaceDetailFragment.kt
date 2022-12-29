package com.starters.yeogida.presentation.place

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.request.place.CommentRequest
import com.starters.yeogida.data.remote.response.CommentData
import com.starters.yeogida.data.remote.response.place.PlaceImg
import com.starters.yeogida.databinding.FragmentPlaceDetailBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.mypage.MyPageActivity
import com.starters.yeogida.presentation.user.profile.UserProfileActivity
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
    private val viewModel: PlaceViewModel by viewModels()
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private lateinit var token: String
    private var memberId by Delegates.notNull<Long>()
    private var placeId: Long = 0
    private var tripId: Long = 0
    private var isMyPost = false

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

        setUserProfileClicked()
        setOnBackPressed(true)
        initAuthorization()
        initPlaceData()
        checkActiveAndLength()
    }

    private fun setUserProfileClicked() {
        viewModel.openUserProfileEvent.observe(
            viewLifecycleOwner,
            EventObserver { memberId ->
                CoroutineScope(Dispatchers.IO).launch {
                    val myMemberId = dataStore.memberId.first()
                    if (myMemberId != memberId) {
                        Intent(requireContext(), UserProfileActivity::class.java).apply {
                            putExtra("memberId", memberId)
                            startActivity(this)
                        }
                    } else {
                        Intent(requireContext(), MyPageActivity::class.java).apply {
                            putExtra("memberId", memberId)
                            startActivity(this)
                        }
                    }
                }
            }
        )
    }

    private fun initAuthorization() {
        CoroutineScope(Dispatchers.IO).launch {
            token = dataStore.userBearerToken.first()
            memberId = dataStore.memberId.first()
        }
    }

    private fun initPlaceData() {
        placeId = requireArguments().getLong("placeId")
        if (requireArguments().getString("type") == "comment_alarm") {
            setOnBackPressed(false)
        }
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
                        tripId = data.tripId
                        withContext(Dispatchers.Main) {
                            binding.place = data
                            if (data.placeImgs[0].imgUrl == "https://yeogida-bucket.s3.ap-northeast-2.amazonaws.com/default_place.png") {
                                binding.viewpagerPlaceToolbar.visibility = View.GONE
                            }
                            isMyPost(data.memberId)
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
    }

    private fun isMyPost(id: Long) {
        if (memberId == id) {
            isMyPost = true
        }
    }

    private fun setOnBackPressed(boolean: Boolean) {
        when (boolean) {
            true -> {
                // 뒤로가기 리스너
                binding.tbPlaceDetail.setNavigationOnClickListener {
                    findNavController().navigateUp()
                }

                val onBackPressedCallback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // 뒤로가기 클릭 시 실행시킬 코드 입력
                        findNavController().navigateUp()
                    }
                }

                // Android 시스템 뒤로가기를 하였을 때, 콜백 설정
                requireActivity().onBackPressedDispatcher.addCallback(
                    viewLifecycleOwner,
                    onBackPressedCallback
                )
            }
            false -> {
                binding.tbPlaceDetail.setNavigationOnClickListener {
                    (activity as PlaceActivity).finish()
                }

                val onBackPressedCallback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // 뒤로가기 클릭 시 실행시킬 코드 입력
                        (activity as PlaceActivity).finish()
                    }
                }

                // Android 시스템 뒤로가기를 하였을 때, 콜백 설정
                requireActivity().onBackPressedDispatcher.addCallback(
                    viewLifecycleOwner,
                    onBackPressedCallback
                )
            }
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
                            memberId,
                            viewModel
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
        setCommentCustomDialog("정말 삭제하시겠습니까?", "삭제", commentId)
    }

    private fun initReportDialog(commentId: Long) {
        setCommentCustomDialog("정말 신고하시겠습니까?", "신고", commentId)
    }

    private fun setCommentCustomDialog(title: String, positive: String, commentId: Long) {
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

    private fun setCustomDialog() {
        CustomDialog(requireContext()).apply {
            showDialog()
            setTitle("정말 삭제하시겠습니까?")
            setPositiveBtn("삭제") {
                YeogidaClient.placeService.deletePlace(
                    token,
                    placeId
                ).customEnqueue(
                    onSuccess = {
                        when (it.code) {
                            200 -> {
                                requireContext().shortToast("장소가 삭제되었습니다.")
                                findNavController().navigateUp()
                            }
                            // TODO: 에러 처리 추가
                            else -> requireContext().shortToast("에러입니다.")
                        }
                    }
                )
                dismissDialog()
            }
            setNegativeBtn("취소") {
                dismissDialog()
            }
        }
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

    fun showBottomSheet(view: View) {
        val bottomSheetDialog = MoreBottomSheetFragment("detail", isMyPost) {
            when (it) {
                "수정" -> requireContext().shortToast("준비중입니다.")
                "삭제" -> {
                    setCustomDialog()
                }
            }
        }
        bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
    }
}
