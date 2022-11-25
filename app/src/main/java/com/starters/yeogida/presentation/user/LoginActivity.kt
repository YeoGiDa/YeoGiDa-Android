package com.starters.yeogida.presentation.user

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.R
import com.starters.yeogida.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.layoutLoginKakao.setOnClickListener {

            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e("LoginActivity", "로그인 실패", error)
                }
                else if (token != null) {
                    Log.i("LoginActivity", "로그인 성공 ${token.accessToken}")

                    UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                        if (error != null) {
                            Log.e("LoginActivity", "토큰 정보 보기 실패", error)
                        }
                        else if (tokenInfo != null) {
                            Log.i("LoginActivity", "토큰 정보 보기 성공" +
                                    "\n회원번호: ${tokenInfo.id}" +
                                    "\n만료시간: ${tokenInfo.expiresIn} 초" +
                                    "\n앱 ID : ${tokenInfo.appId}")
                        }
                    }
                    /*
                    UserApiClient.instance.unlink { error ->
                        if (error != null) {
                            Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                        }else {
                            Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                        }
                    }*/
                }
            }


        }

    }
}