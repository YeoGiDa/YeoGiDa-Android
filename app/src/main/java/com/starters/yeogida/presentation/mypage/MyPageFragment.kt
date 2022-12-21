package com.starters.yeogida.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.api.UserService
import com.starters.yeogida.data.remote.common.TokenData
import com.starters.yeogida.databinding.FragmentMyPageBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.trip.AddTripActivity
import com.starters.yeogida.presentation.user.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMyPageBinding
    private val viewModel: MyPageViewModel by viewModels()
    private val dataStore = YeogidaApplication.getInstance().getDataStore()
    private val userService: UserService = YeogidaClient.userService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.view = this

        initClickListener()
        setLogout()
        setWithDrawDialog()
    }

    private fun initClickListener() {
        binding.tbMyPage.setNavigationOnClickListener {
            (activity as MyPageActivity).finish()
        }
    }

    private fun setWithDrawDialog() {
        viewModel.withDrawDialogEvent.observe(viewLifecycleOwner) {
            // 다이얼로그 띄우기
            val dialog = CustomDialog(requireContext()).apply {
                showDialog()
                setTitle("정말 탈퇴하시겠습니까?")

                setPositiveBtn("탈퇴") {
                    dismissDialog()

                    CoroutineScope(Dispatchers.IO).launch {
                        val userAccessToken = dataStore.userAccessToken.first()
                        val userRefreshToken = dataStore.userRefreshToken.first()
                        withDrawUser(userAccessToken, userRefreshToken)
                    }
                }
                setNegativeBtn("취소") {
                    dismissDialog()
                }
            }
        }
    }

    private suspend fun withDrawUser(userAccessToken: String, userRefreshToken: String) {
        val withDrawResponse = userService.withDrawUser(dataStore.userBearerToken.first())
        Log.e("withDrawUser", "$withDrawResponse")

        when (withDrawResponse.code()) {
            200 -> {
                Log.e("withDrawUser", "code : 200, 회원 탈퇴 성공")

                // DataStore 값 지우기
                removeUserData()

                // 카카오 계정 연결 끊기
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        Log.e("withDrawUser", "카카오 계정 연결 끊기 실패", error)
                    } else {
                        Log.i("withDrawUser", "카카오 계정 연결 끊기 성공")
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                    // 로그인 화면으로
                    moveToLogin()
                }
            }

            403 -> { // 권한 없음, 만료된 토큰
                Log.e("withDrawUser/Error", withDrawResponse.body()?.message.toString())
                validateToken(userAccessToken, userRefreshToken)
            }

            404 -> {
                // 존재하지 않는 회원
                Log.e("UserWithDraw/Error", "${withDrawResponse.body()?.code}")
                Log.e("UserWithDraw/Error", "${withDrawResponse.body()?.message}")
            }

            else -> {
                Log.e("UserWithDraw/Error", "${withDrawResponse.body()?.code}")
            }
        }
    }

    private suspend fun validateToken(userAccessToken: String, userRefreshToken: String) {
        val tokenResponse = userService.validateToken(
            TokenData(
                userAccessToken,
                userRefreshToken
            )
        )

        when (tokenResponse.code()) {
            200 -> {
                // 유효한 Access, Refresh
                Log.e("MyPage/TokenValidation", "200")

                // 토큰 값을 전달하여 탈퇴
                withDrawUser(userAccessToken, userRefreshToken)
            }

            201 -> { // 만료된 Access
                // newAccessToken 을 사용해서 회원 탈퇴 진행
                Log.e("MyPage/TokenValidation", "201")
                val newAccessToken = tokenResponse.body()?.data?.newAccessToken.toString()
                val refreshToken = tokenResponse.body()?.data?.refreshToken.toString()

                dataStore.saveUserToken(newAccessToken, refreshToken)
                withDrawUser(newAccessToken, refreshToken)
            }

            403 -> { // Access, Refresh 둘 다 만료 or 비정상적인 형식의 토큰
                Log.e("MyPage/TokenValidation", "403")
                removeUserData()

                withContext(Dispatchers.Main) {
                    moveToLogin()
                    Toast.makeText(
                        requireContext(),
                        "계정이 만료되었습니다. 다시 로그인 해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            else -> {
                Log.e("MyPage/TokenValidation", "$tokenResponse")
            }
        }
    }

    private fun setLogout() {
        viewModel.logOutEvent.observe(viewLifecycleOwner) {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("UserLogout", "로그아웃 실패", error)
                } else {
                    Log.i("UserLogout", "로그아웃 성공")
                    Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        // DataStore 값 지우기
                        removeUserData()
                    }

                    // 로그인 화면으로
                    moveToLogin()
                }
            }
        }
    }

    private suspend fun removeUserData() {
        dataStore.saveIsLogin(false)
        dataStore.removeUserToken()
        dataStore.removeMemberId()
    }

    private fun moveToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun moveToNotification(view: View) {
        findNavController().navigate(R.id.action_mypage_to_alarm)
    }

    fun moveToCommentPlace(view: View) {
        findNavController().navigate(R.id.action_mypage_to_comment)
    }

    fun moveToMyTrip(view: View) {
        findNavController().navigate(R.id.action_mypage_to_my_trip)
    }
}
