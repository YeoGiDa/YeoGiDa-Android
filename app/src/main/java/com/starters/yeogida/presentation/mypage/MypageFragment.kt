package com.starters.yeogida.presentation.around

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentMypageBinding
import com.starters.yeogida.presentation.common.CustomDialog
import com.starters.yeogida.presentation.mypage.MyPageViewModel
import com.starters.yeogida.presentation.user.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MypageFragment : Fragment() {

    private lateinit var binding: FragmentMypageBinding
    private val viewModel: MyPageViewModel by viewModels()
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setLogout()
        setWithDrawDialog()
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
                        // TODO. API 사용하여 서버 회원 제거

                        // DataStore 값 지우기
                        removeUserPref()

                        // 카카오 계정 연결 끊기
                        UserApiClient.instance.unlink { error ->
                            if (error != null) {
                                Log.e("UserWithDraw", "카카오 계정 연결 끊기 실패", error)
                            } else {
                                Log.i("UserWithDraw", "카카오 계정 연결 끊기 성공")
                            }
                        }
                    }

                    // 로그인 화면으로
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                setNegativeBtn("취소") {
                    dismissDialog()
                }
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
                    CoroutineScope(Dispatchers.IO).launch {
                        // DataStore 값 지우기
                        removeUserPref()
                    }

                    // 로그인 화면으로
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }

    private suspend fun removeUserPref() {
        dataStore.saveRefreshToken("")
        dataStore.saveAccessToken("")
        dataStore.saveIsLogin(false)
    }
}