package com.starters.yeogida.data.local

data class LikeTripData(
    val tripId: Long,
    val region: String,
    val imageUrl: String,
    val title: String,
    val subTitle: String,
    val nickname: String,
    val isLike: Boolean,
    val likeCount: Int,
    val placeCount: Int,
)
