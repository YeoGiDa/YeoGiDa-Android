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
import com.starters.yeogida.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("LoginActivity", "로그인 실패 $error")
        } else if (token != null) {
            Log.e("LoginActivity", "로그인 성공 ${token.accessToken}")
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
                Log.e("LoginActivity", "로그인 실패", error)
            } else if (token != null) {
                Log.i("LoginActivity", "로그인 성공 ${token.accessToken}")

                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Log.e("LoginActivity", "토큰 정보 보기 실패", error)
                    } else if (tokenInfo != null) {
                        Log.i(
                            "LoginActivity", "토큰 정보 보기 성공" +
                                    "\n회원번호: ${tokenInfo.id}" +
                                    "\n만료시간: ${tokenInfo.expiresIn} 초" +
                                    "\n앱 ID : ${tokenInfo.appId}"
                        )
                    }
                }

                UserApiClient.instance.me { user, error ->
                    user?.kakaoAccount?.let { userAccount ->

                        val email = userAccount.email.toString().trim()

                        if( isJoinedMember(email) ) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else  { // 가입 안되어있으면
                            val intent = Intent(this, JoinActivity::class.java)
                            intent.putExtra("email", email)

                            // 닉네임
                            userAccount.profile?.nickname?.let {
                                intent.putExtra( "nickname", it.trim())
                            }

                            // 프로필 사진
                            userAccount.profile?.profileImageUrl?.let {
                                intent.putExtra("profileImageUrl", it.trim())
                            }

                            startActivity(intent)
                            finish()
                        }
                    }
                }

            }
        }
    }

    private fun isJoinedMember( email : String ) : Boolean {
        var isJoined = false
        // TODO. 서버 이메일 전달해서 가입되어있는 사용자인지 확인하기
        return isJoined
    }
}