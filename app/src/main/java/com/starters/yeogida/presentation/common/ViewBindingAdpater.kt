package com.starters.yeogida.presentation.common

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isLike")
fun updateVisibility(view: View, isLike: Boolean) {
    view.isSelected = isLike
}

@BindingAdapter("writerId", "myId")
fun userViewVisibility(view: TextView, writerId: Long, myId: Long) {
    view.isVisible = writerId == myId
}

@BindingAdapter("writerId", "memberId")
fun userViewGone(view: TextView, writerId: Long, memberId: Long) {
    view.isVisible = writerId != memberId
}