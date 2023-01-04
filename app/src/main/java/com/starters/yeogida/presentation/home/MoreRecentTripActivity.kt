package com.starters.yeogida.presentation.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.starters.yeogida.databinding.ActivityMoreTripBinding

class MoreRecentTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMoreTripBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreTripBinding.inflate(layoutInflater)
        initTitle()
        setContentView(binding.root)
    }

    private fun initTitle() {
        binding.tvMoreTrip.text = "팔로잉의 최근 여행지"
    }
}
