package com.starters.yeogida.data.interceptor

import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.common.ValidateTokenResponse
import com.starters.yeogida.presentation.user.LoginActivity
import gun0912.tedimagepicker.util.ToastUtil.showToast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val baseUrl: String) : Interceptor {
    private val dataStore = YeogidaApplication.getInstance().getDataStore()
    private var accessToken = ""
    private var refreshToken = ""

    private fun initToken() {
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.userAccessToken.first()
            refreshToken = dataStore.userRefreshToken.first()
        }
    }

    private fun saveToken(accessToken: String, refreshToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveUserToken(accessToken, refreshToken)
        }
    }

    private fun deleteToken() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveIsLogin(false)
            dataStore.removeUserToken()
            dataStore.removeMemberId()
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        initToken()

        val request = chain.request()
        Log.d("AuthInterceptor", "request : $request")

        val response = chain.proceed(request)
        Log.d("AuthInterceptor", "response : $response")

        when (response.code) {
            // accessToken 만료
            401 -> {
                response.close()
                val appContext = YeogidaApplication.context()

                // 토큰 유효성 확인
                val refreshTokenRequest = request.newBuilder().get()
                    .url("${baseUrl}token/validate")
                    .addHeader("accesstoken", accessToken)
                    .addHeader("refreshtoken", refreshToken)
                    .build()
                val refreshTokenResponse = chain.proceed(refreshTokenRequest)

                // 토큰 만료 확인
                if (refreshTokenResponse.isSuccessful) {
                    // accessToekn 만료, refresh 토큰은 유효
                    val responseRefresh = Gson().fromJson(
                        refreshTokenResponse.body?.string(),
                        ValidateTokenResponse::class.java
                    )

                    // dataStore에 저장
                    saveToken(responseRefresh.data.newAccessToken, responseRefresh.data.refreshToken)

// 바뀐 accessToken으로 요청보내기
                    val newRequest = request.newBuilder()
                        .header("Authorization", "Bearer ${responseRefresh.data.newAccessToken}")
                        .build()
                    return chain.proceed(newRequest)
                } else {
                    // refresh token도 만료가 된 경우
                    with(appContext) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Intent(appContext, LoginActivity::class.java).also {
                                it.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(it)
                            }
                            showToast("자동로그인이 만료되었습니다.")

                            // dataStore 저장값 비우기
                            deleteToken()

                            Runtime.getRuntime().exit(0)
                            cancel()
                        }
                    }
                }
            }
        }

        return response
    }
}
