package com.starters.yeogida.presentation.user

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.MainActivity
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.request.LoginRequestData
import com.starters.yeogida.databinding.ActivityLoginBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.CustomProgressDialog
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val dataStore = YeogidaApplication.getInstance().getDataStore()
    private val userService = YeogidaClient.userService
    private lateinit var fcmToken: String
    private lateinit var progressDialog: CustomProgressDialog

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            when {
                error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                    Log.d("에러", "접근이 거부 됨(동의 취소)")
                }
                error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                    Log.d("에러", "유효하지 않는 앱")
                }
                error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                    Log.d("에러", "인증 수단이 유효하지 않아 인증할 수 없는 상태")
                }
                error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                    Log.d("에러", "요청 파라미터 오류")
                }
                error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                    Log.d("에러", "유효하지 않은 scope ID")
                }
                error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                    Log.d("에러", "설정이 올바르지 않음(android key hash)")
                }
                error.toString() == AuthErrorCause.ServerError.toString() -> {
                    Log.d("에러", "서버 내부 에러")
                }
                error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                    Log.d("에러", "앱이 요청 권한이 없음")
                }
                else -> { // Unknown
                    Log.d("에러", "기타 에러")
                }
            }
        } else if (token != null) {
            progressDialog.showDialog()
            Log.e("카카오 로그인", "로그인 성공 ${token.accessToken}")
            Log.e("카카오 로그인", "accessToken : ${token.accessToken}")
            Log.e("카카오 로그인", "refreshToken : ${token.refreshToken}")

            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.e("카카오 로그인", "토큰 정보 보기 실패", error)
                } else if (tokenInfo != null) {
                    Log.i(
                        "카  카오 로그인",
                        "토큰 정보 보기 성공" +
                                "\n회원번호: ${tokenInfo.id}" +
                                "\n만료시간: ${tokenInfo.expiresIn} 초" +
                                "\n앱 ID : ${tokenInfo.appId}"
                    )

                    UserApiClient.instance.me { user, error ->
                        user?.kakaoAccount?.let { userAccount ->

                            val email = userAccount.email.toString()
                            val userNum = tokenInfo.id.toString()
                            val nickname = userAccount.profile?.nickname
                            val profileImageUrl = userAccount.profile?.profileImageUrl

                            CoroutineScope(Dispatchers.IO).launch {
                                postLogin(email, userNum, nickname, profileImageUrl)
                            }
                        }
                    }
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.view = this

        initUnderLine()
        progressDialog = CustomProgressDialog(this)

        binding.btnLoginKakao.setOnClickListener {
            getFCMToken()
        }

        binding.btnLoginNaver.setOnClickListener {
            shortToast("구현 준비 중입니다")
        }

        binding.btnLoginGoogle.setOnClickListener {
            shortToast("구현 준비 중입니다")
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("token", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                fcmToken = task.result

                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                    UserApiClient.instance.loginWithKakaoTalk(this, callback = mCallback)
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
                }

                // Log and toast
                Log.d("token", "FCM Token is $fcmToken")
            }
        )
    }

    private suspend fun postLogin(
        email: String,
        userNum: String,
        nickname: String?,
        profileImageUrl: String?
    ) {
        Log.d("카카오 로그인", "email = $email")
        Log.d("카카오 로그인", "회원번호 = $userNum")

        val loginResponse = userService.postLogin(
            LoginRequestData(
                email,
                userNum,
                fcmToken
            )
        )

        Log.d("loginResponse", "$loginResponse")

        when (loginResponse.code()) {
            200 -> {
                val accessToken = loginResponse.body()?.data?.accessToken
                val refreshToken = loginResponse.body()?.data?.refreshToken
                val memberId = loginResponse.body()?.data?.memberId

                dataStore.saveIsLogin(true)
                dataStore.saveUserToken(accessToken, refreshToken)
                dataStore.saveMemberId(memberId)

                Log.e("Login/userAccessToken", dataStore.userAccessToken.first())
                Log.e("Login/userRefreshToken", dataStore.userRefreshToken.first())
                Log.e("Login/userMemberId", dataStore.memberId.first().toString())

                withContext(Dispatchers.Main) {
                    progressDialog.dismissDialog()
                    startMain()
                }
            }
            404 -> { // 회원이 아닐 때
                Log.e("loginResponse", "$loginResponse")

                withContext(Dispatchers.Main) {
                    progressDialog.dismissDialog()
                    startJoin(email, userNum, nickname, profileImageUrl)
                }
            }
            else -> {
                Log.e("loginResponse/Error", loginResponse.message())
                progressDialog.dismissDialog()
            }
        }
    }

    private fun startJoin(
        email: String,
        userNum: String,
        nickname: String?,
        profileImageUrl: String?
    ) {
        Intent(this@LoginActivity, JoinActivity::class.java).apply {
            putExtra("email", email) // 이메일
            putExtra("userNum", userNum) // 회원번호
            putExtra("fcmToken", fcmToken)

            nickname?.let {
                putExtra("nickname", it) // 닉네임
            }

            profileImageUrl?.let {
                putExtra("profileImageUrl", it) // 프로필 이미지 URL
            }

            startActivity(this)
        }
    }

    private fun startMain() {
        startActivity(
            Intent(
                this@LoginActivity,
                MainActivity::class.java
            )
        )
        finish()
    }

    // 약관 밑줄
    private fun initUnderLine() {
        with(binding) {
            tvLoginTerms.paintFlags = tvLoginTerms.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            tvLoginTermsPersonal.paintFlags = tvLoginTermsPersonal.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
    }

    // 인터넷 창 생성
    private fun openInternetSite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    fun openTermsPage(view: View) {
        openInternetSite("https://yeogida.notion.site/e1197727840a4ac0a382f53b43d371e6")
    }

    fun openPersonalPage(view: View) {
        openInternetSite("https://yeogida.notion.site/dcbf68223c254d2990d9076bab1624ec")
    }
}
