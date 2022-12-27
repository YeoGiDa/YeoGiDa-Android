package com.starters.yeogida.data.remote.response.trip

data class TripInfoResponse(
    val memberId: Long,
    val profileImgUrl: String,
    val nickname: String,
    val isLike: Boolean,
    val title: String,
    val subTitle: String,
    val imgUrl: String,
    val heartCount: Int,
    val placeCount: Int
)
