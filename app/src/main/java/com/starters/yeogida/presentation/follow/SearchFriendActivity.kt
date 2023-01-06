package com.starters.yeogida.presentation.follow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.starters.yeogida.databinding.ActivitySearchFriendBinding

class SearchFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}