package com.starters.yeogida.presentation.user.profile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.starters.yeogida.R

class UserProfileActivity : AppCompatActivity() {

    private var memberId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        getMemberId()
    }

    private fun getMemberId() {
        memberId = intent.getLongExtra("memberId", 0)
        Log.e("UserProfileActivity", memberId.toString())

        intent.putExtra("memberId", memberId)
    }
}