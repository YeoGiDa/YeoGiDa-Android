package com.starters.yeogida.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.starters.yeogida.presentation.user.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel : SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        viewModel.isLoading.observe(this) { isLoading ->
            splashScreen.setKeepOnScreenCondition { isLoading }
            if( !isLoading ) {
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                }
                finish()
            }
        }
    }
}