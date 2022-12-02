package com.starters.yeogida.presentation.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.Account
import com.starters.yeogida.MainActivity
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.request.LoginRequestData
import com.starters.yeogida.databinding.ActivityLoginBinding
import com.starters.yeogida.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

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

                                val userService = ApiClient.userService

                                CoroutineScope(Dispatchers.IO).launch {
                                    Log.d("카카오 로그인", "email = ${userAccount.email}")
                                    Log.d("카카오 로그인", "회원번호 = ${tokenInfo.id}")

                                    val loginResponse = userService.postLogin(
                                        LoginRequestData(
                                            userAccount.email.toString(),
                                            tokenInfo.id.toString()
                                        )
                                    )
                                    Log.d("loginResponse", "$loginResponse")

                                    if (loginResponse.isSuccessful) {
                                        Log.d("loginResponse", "$loginResponse")
                                        dataStore.saveAccessToken(loginResponse.body()!!.data!!.accessToken)
                                        dataStore.saveRefreshToken(loginResponse.body()!!.data!!.refreshToken)
                                        dataStore.saveIsLogin(true)

                                        withContext(Dispatchers.Main) {
                                            Log.e(
                                                "Login/userAccessToken",
                                                dataStore.userAccessToken.first()
                                            )
                                            Log.e(
                                                "Login/userRefreshToken",
                                                dataStore.userRefreshToken.first()
                                            )
                                            startMain()
                                        }

                                    } else if (loginResponse.code() == 404) { // 회원이 아닐 때
                                        Log.e("loginResponse", "$loginResponse")
                                        withContext(Dispatchers.Main) {
                                            startJoin(userAccount, tokenInfo)
                                        }
                                    } else { // 서버 에러
                                        Log.e("loginResponse", "$loginResponse")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startJoin(
        userAccount: Account,
        tokenInfo: AccessTokenInfo
    ) {
        val intent = Intent(this@LoginActivity, JoinActivity::class.java)

        intent.putExtra("email", userAccount.email)     // 이메일
        intent.putExtra("userNum", tokenInfo.id.toString()) // 회원번호

        // 닉네임
        userAccount.profile?.nickname?.let { nickname ->
            intent.putExtra("nickname", nickname)
        }

        // 프로필 사진
        userAccount.profile?.profileImageUrl?.let { profileImageUrl ->
            intent.putExtra("profileImageUrl", profileImageUrl)
        }

        startActivity(intent)
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