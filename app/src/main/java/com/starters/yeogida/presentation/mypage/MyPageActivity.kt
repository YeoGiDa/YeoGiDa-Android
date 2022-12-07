package com.starters.yeogida.presentation.mypage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.starters.yeogida.databinding.ActivityMyPageBinding

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
