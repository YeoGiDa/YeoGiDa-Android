package com.starters.yeogida.presentation.splash

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.starters.yeogida.MainActivity
import com.starters.yeogida.presentation.user.LoginActivity
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= S) {
            installSplashScreen().apply {
                setKeepOnScreenCondition { isReady }
            }
        }
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {
        viewModel.isLoading.observe(this) { isLoading ->
            isReady = !isLoading
            Log.e("isReady => ", "$isReady")
            if (isReady) {
                checkLoginAndStart()
            }
        }
    }

    private fun checkLoginAndStart() {
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
        thread(start = true) {
            Intent(this, LoginActivity::class.java).also {
                it.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
            }
        }
    }

    private fun startMain() {
        thread(start = true) {
            Intent(this, MainActivity::class.java).also {
                it.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
            }
        }
    }
}