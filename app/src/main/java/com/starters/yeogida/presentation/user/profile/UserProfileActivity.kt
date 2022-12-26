package com.starters.yeogida.presentation.user.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
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

        val bundle = bundleOf("memberId" to memberId)
        UserProfileFragment().apply {
            arguments = bundle
        }
    }
}