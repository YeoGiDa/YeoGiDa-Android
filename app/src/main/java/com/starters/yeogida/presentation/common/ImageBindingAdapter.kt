package com.starters.yeogida.presentation.common

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.starters.yeogida.GlideApp
import java.io.File

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(imageView)
            .load(imageUrl)
            .into(imageView)
    }
}

@BindingAdapter("circleImageUrl")
fun loadCircleImage(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(imageView)
            .load(imageUrl)
            .circleCrop()
            .into(imageView)
    }
}

@BindingAdapter("imageUri")
fun loadImageWithUri(imageView: ImageView, imageUri: Uri?) {
    imageUri?.let {
        GlideApp.with(imageView)
            .load(it)
            .into(imageView)
    }
}

@BindingAdapter("imageFile")
fun loadImageWithBitmap(imageView: ImageView, imageFile: File?) {
    imageFile?.let {
        GlideApp.with(imageView)
            .load(imageFile)
            .into(imageView)
    }
}