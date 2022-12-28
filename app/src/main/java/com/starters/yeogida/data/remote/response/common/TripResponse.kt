package com.starters.yeogida.data.remote.response.common

data class TripResponse(
    val tripId: Long,
    val memberId: Long,
    val nickname: String,
    val region: String,
    val title: String,
    val subTitle: String,
    val imgUrl: String,
    val heartCount: Int,
    val placeCount: Int
)