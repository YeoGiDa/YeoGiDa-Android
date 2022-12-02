package com.starters.yeogida.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.starters.yeogida.MainActivity
import com.starters.yeogida.presentation.user.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel : SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        viewModel.isLoading.observe(this) { isLoading ->
            splashScreen.setKeepOnScreenCondition { isLoading }
        }

        viewModel.isLogin.observe(this) { isLogin ->
            if (isLogin) {
                Log.e("Splash/isLogin", "isLogin => true")
                startMain()
            } else {
                Log.e("Splash/isLogin", "isLogin => false")
                startLogin()
            }
        }
    }

    private fun startLogin() {
        Intent(this, LoginActivity::class.java).also {
            it.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }

    private fun startMain() {
        Intent(this, MainActivity::class.java).also {
            it.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }
}