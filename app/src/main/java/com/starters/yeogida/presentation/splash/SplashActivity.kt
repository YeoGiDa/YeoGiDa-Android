package com.starters.yeogida.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.TokenManagerProvider
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.MainActivity
import com.starters.yeogida.presentation.user.LoginActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val viewModel : SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Data Store에 JWT 토큰이 발급되어
        lifecycleScope.launch {
            YeogidaApplication.getInstance().getDataStore().userJWTAccessToken.collect {
                Log.d("Splash/DSJWTAccessToken", it)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            splashScreen.setKeepOnScreenCondition { isLoading }

            if( !isLoading ) {
                // 로그인되어있는 사용자이면, MainActivity
                startLogin()
                finish()
            }
        }
    }

    private fun startLogin() {
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun startMain() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
        }
    }
}