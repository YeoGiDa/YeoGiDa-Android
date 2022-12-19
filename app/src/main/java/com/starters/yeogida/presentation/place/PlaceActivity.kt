package com.starters.yeogida.presentation.place

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.starters.yeogida.databinding.ActivityPlaceBinding

class PlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceBinding.inflate(layoutInflater)
        getTripId()
        setContentView(binding.root)
    }

    private fun getTripId() {
        val tripId = intent.getLongExtra("tripId", 0)
        intent.putExtra("tripId", tripId)
    }
}
