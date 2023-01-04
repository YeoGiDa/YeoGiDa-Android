package com.starters.yeogida.presentation.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.starters.yeogida.R
import com.starters.yeogida.databinding.ActivityMoreTravelerBinding

class MoreTravelerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMoreTravelerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreTravelerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}