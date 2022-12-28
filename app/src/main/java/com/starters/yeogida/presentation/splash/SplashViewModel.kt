package com.starters.yeogida.presentation.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.common.TokenData
import com.starters.yeogida.network.YeogidaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    private val userService = YeogidaClient.userService
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private var userAccessToken: String = ""
    private var userRefreshToken: String = ""

    init {
        viewModelScope.launch {
            _isLogin.value = dataStore.userIsLogin.first()
            _isLoading.value = true

            userAccessToken = dataStore.userAccessToken.first()
            userRefreshToken = dataStore.userRefreshToken.first()

            /*Log.e("SplashViewModel/init/userAccessToken", userAccessToken)
            Log.e("SplashViewModel/init/userRefreshToken", userRefreshToken)*/

            validateToken(TokenData(userAccessToken, userRefreshToken))
        }
    }

    private suspend fun validateToken(tokenData: TokenData) {
        /*Log.e("SplashViewModel/validateToken/userAccessToken", tokenData.accessToken )
        Log.e("SplashViewModel/validateToken/userRefreshToken", tokenData.refreshToken )*/

        CoroutineScope(Dispatchers.IO).launch {
            if (tokenData.accessToken.isNotEmpty() && tokenData.refreshToken.isNotEmpty()) {
                val response = userService.validateToken(tokenData)
                Log.e("SplashViewModel/validateToken/response", "$response")

                launch {
                    when (response.code()) {
                        200 -> { // 유효한 Access, Refresh
                            _isLogin.postValue(true)

                            dataStore.saveIsLogin(true)
                        }
                        201 -> { // 만료된 Access, Refresh 동일
                            _isLogin.postValue(true)

                            val newAccessToken = response.body()?.data?.newAccessToken
                            val refreshToken = response.body()?.data?.refreshToken

                            dataStore.saveIsLogin(true)
                            dataStore.saveUserToken(newAccessToken, refreshToken)
                        }
                        403 -> { // Access, Refresh 둘 다 만료 or 비정상적인 형식의 토큰
                            _isLogin.postValue(false)

                            Log.e("SplashViewModel/403", "403")
                            removeUserData()
                        }

                        404 -> {
                            // DataStore 에 있는 회원 정보가 DB에 있는 회원이 아닐 때(밴 당했을 때)
                            _isLogin.postValue(false)

                            Log.e("SplashViewModel/404", "404")
                            removeUserData()

                            // 다시 이메일을 사용하여 가입하려 할 때, 가입을 막거나 할 수 있을듯

                            // 소셜 로그인 계정 해제
                            // 카카오
                            UserApiClient.instance.unlink { }
                        }
                        else -> {}
                    }
                }.join()

            } else {
            }
            _isLoading.postValue(false) // 스플래시 로딩 종료
        }
    }

    private suspend fun removeUserData() {
        dataStore.saveIsLogin(false)
        dataStore.removeUserToken()
        dataStore.removeMemberId()
    }
}