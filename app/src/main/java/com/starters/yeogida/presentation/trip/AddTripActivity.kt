package com.starters.yeogida.presentation.trip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.starters.yeogida.databinding.ActivityAddTripBinding

class AddTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTripBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
