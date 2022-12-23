package com.starters.yeogida.presentation.common

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

// 여행지 좋아요 버튼
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

// 유저 상세 팔로우 버튼
@BindingAdapter("isFollow")
fun updateFollowBtnBackground(followBtn: AppCompatButton, isFollow: Boolean) {
    followBtn.isSelected = isFollow
    if (isFollow) followBtn.text = "팔로잉"
    else followBtn.text = "팔로우"
}