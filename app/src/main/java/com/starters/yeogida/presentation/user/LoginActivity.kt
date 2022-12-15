package com.starters.yeogida.presentation.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.MainActivity
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.request.LoginRequestData
import com.starters.yeogida.databinding.ActivityLoginBinding
import com.starters.yeogida.network.YeogidaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val dataStore = YeogidaApplication.getInstance().getDataStore()
    private val userService = YeogidaClient.userService

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("카카오 로그인", "로그인 실패 $error")
        } else if (token != null) {
            Log.e("카카오 로그인", "로그인 성공 ${token.accessToken}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.layoutLoginKakao.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                loginWithKakaoTalk()
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
            }
        }
    }

    private fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if (error != null) {
                Log.e("카카오 로그인", "로그인 실패", error)
            } else if (token != null) {
                Log.e("카카오 로그인", "accessToken : ${token.accessToken}")
                Log.e("카카오 로그인", "refreshToken : ${token.refreshToken}")

                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Log.e("카카오 로그인", "토큰 정보 보기 실패", error)
                    } else if (tokenInfo != null) {
                        Log.i(
                            "카카오 로그인", "토큰 정보 보기 성공" +
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

                                CoroutineScope(Dispatchers.Main).launch {
                                    postLogin(email, userNum, nickname, profileImageUrl)
                                }
                            }
                        }
                    }
                }
            }
        }
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
                userNum
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
                    startMain()
                }
            }
            404 -> { // 회원이 아닐 때
                Log.e("loginResponse", "$loginResponse")

                withContext(Dispatchers.Main) {
                    startJoin(email, userNum, nickname, profileImageUrl)
                }
            }
            else -> {
                Log.e("loginResponse/Error", loginResponse.message())
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

            nickname?.let {
                putExtra("nickname", it)  // 닉네임
            }

            profileImageUrl?.let {
                putExtra("profileImageUrl", it)    // 프로필 이미지 URL
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
}