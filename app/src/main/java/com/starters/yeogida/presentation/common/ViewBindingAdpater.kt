package com.starters.yeogida.presentation.common

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isLike")
fun updateVisibility(view: View, isLike: Boolean) {
    view.isSelected = isLike
}