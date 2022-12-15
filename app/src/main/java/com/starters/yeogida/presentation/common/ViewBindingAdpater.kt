package com.starters.yeogida.presentation.common

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isLike")
fun updateVisibility(view: View, isLike: Boolean) {
    view.isSelected = isLike
}

@BindingAdapter("writerId", "memberId")
fun userViewVisibility(view: View, writerId: Long, memberId: Long) {
    view.isVisible = writerId == memberId
}