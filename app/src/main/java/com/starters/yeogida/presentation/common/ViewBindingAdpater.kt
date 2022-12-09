package com.starters.yeogida.presentation.common

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("isLike")
fun updateVisibility(view: View, isLike: Boolean) {
    view.isSelected = isLike
}

@BindingAdapter("count")
fun convertIntToString(view: TextView, value: Int) {
    view.text = value.toString()
}
