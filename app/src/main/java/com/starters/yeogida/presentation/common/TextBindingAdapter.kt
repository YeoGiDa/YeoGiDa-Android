package com.starters.yeogida.presentation.common

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("double")
fun setDouble(view: TextView, value: Double) {
    view.text = value.toString()
}

@BindingAdapter("count")
fun setInt(view: TextView, value: Int) {
    view.text = value.toString()
}

@BindingAdapter("position", "size")
fun setPhotoCounter(view: TextView, position: String, size: String) {
    view.text = "$position / $size"
}
