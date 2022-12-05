package com.starters.yeogida.presentation.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.common.TokenData
import com.starters.yeogida.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    private val userService = ApiClient.userService
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private var userAccessToken: String = ""
    private var userRefreshToken: String = ""

    init {
        viewModelScope.launch {
            _isLogin.value = dataStore.userIsLogin.first()

            userAccessToken = dataStore.userAccessToken.first()
            userRefreshToken = dataStore.userRefreshToken.first()

            /*Log.e("SplashViewModel/init/userAccessToken", userAccessToken)
            Log.e("SplashViewModel/init/userRefreshToken", userRefreshToken )*/

            validateToken(TokenData(userAccessToken, userRefreshToken))
        }
    }

    private suspend fun validateToken(tokenData: TokenData) {
        /*Log.e("SplashViewModel/validateToken/userAccessToken", tokenData.accessToken )
        Log.e("SplashViewModel/validateToken/userRefreshToken", tokenData.refreshToken )*/

        CoroutineScope(Dispatchers.IO).launch {
            if (!tokenData.accessToken.isNullOrBlank() && !tokenData.refreshToken.isNullOrBlank()) {
                val response = userService.validateToken(tokenData)
                Log.e("SplashViewModel/validateToken/response", "$response")

                launch {
                    when (response.code()) {
                        200 -> { // 유효한 Access, Refresh
                            dataStore.saveIsLogin(true)
                        }
                        201 -> { // 만료된 Access, Refresh 동일
                            dataStore.saveIsLogin(true)
                            dataStore.saveAccessToken(response.body()?.data?.newAccessToken ?: "")
                            dataStore.saveRefreshToken(response.body()?.data?.refreshToken ?: "")
                        }
                        403 -> { // Access, Refresh 둘 다 만료 or 비정상적인 형식의 토큰
                            dataStore.saveIsLogin(false)
                            _isLogin.postValue(dataStore.userIsLogin.first())
                            Log.e("SplashViewModel/403", "403")
                        }
                        else -> {}
                    }
                }.join()

            } else {
            }
            _isLoading.postValue(false) // 스플래시 로딩 종료
        }
    }
}