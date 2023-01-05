package com.starters.yeogida.presentation.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.starters.yeogida.R
import com.starters.yeogida.databinding.ActivityImageBinding
import java.io.File
import java.io.FileInputStream

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
        val imgUrl = intent.getStringExtra("imgUrl")
        val imgPath = intent.getStringExtra("filePath")

        if (uri != null) {
            Glide.with(this).load(uri?.toUri()).into(binding.ivImage)
        } else if (imgUrl != null) {
            Log.e("imgUrl", imgUrl)
            Glide.with(this).load(imgUrl).into(binding.ivImage)
        } else if (imgPath != null) {
            val file = File(imgPath)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeStream(FileInputStream(file), null, options)
            binding.ivImage.setImageBitmap(bitmap)
        }
    }

    fun close(view: View) {
        finish()
    }
}
