package com.starters.yeogida.presentation.place

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.starters.yeogida.databinding.ActivityPlaceBinding

class PlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}