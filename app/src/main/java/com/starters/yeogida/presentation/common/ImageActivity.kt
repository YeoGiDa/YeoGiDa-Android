package com.starters.yeogida.presentation.common

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.starters.yeogida.R
import com.starters.yeogida.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image)
        binding.view = this

        initImageView()

        setContentView(binding.root)
    }

    private fun initImageView() {
        val uri = intent.getStringExtra("imageUri")
        Glide.with(this).load(uri?.toUri()).into(binding.ivImage)
    }

    fun close(view: View) {
        finish()
    }
}