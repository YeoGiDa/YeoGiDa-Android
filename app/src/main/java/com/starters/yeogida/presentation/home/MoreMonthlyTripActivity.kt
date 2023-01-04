package com.starters.yeogida.presentation.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.starters.yeogida.databinding.ActivityMoreTripBinding

class MoreMonthlyTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMoreTripBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}