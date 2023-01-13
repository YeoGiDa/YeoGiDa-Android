package com.starters.yeogida.presentation.common

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.starters.yeogida.GlideApp
import com.starters.yeogida.R
import java.io.File

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(imageView)
            .load(imageUrl)
            .placeholder(R.drawable.progress_animation)
            .into(imageView)
    }
}

@BindingAdapter("circleImageUrl")
fun loadCircleImage(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(imageView)
            .load(imageUrl)
            .circleCrop()
            .placeholder(R.drawable.progress_animation)
            .into(imageView)
    }
}

@BindingAdapter("imageUri")
fun loadImageWithUri(imageView: ImageView, imageUri: Uri?) {
    imageUri?.let {
        GlideApp.with(imageView)
            .load(it)
            .placeholder(R.drawable.progress_animation)
            .into(imageView)
    }
}

@BindingAdapter("imageFile")
fun loadImageWithBitmap(imageView: ImageView, imageFile: File?) {
    imageFile?.let {
        GlideApp.with(imageView)
            .load(imageFile)
            .placeholder(R.drawable.progress_animation)
            .into(imageView)
    }
}