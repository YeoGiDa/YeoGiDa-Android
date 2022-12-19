package com.starters.yeogida.data.remote.response.trip

data class PostTripResponse(
    val tripId: Long,
    val memberId: Long,
    val region: String,
    val title: String,
    val subTitle: String,
    val imgUrl: String,
    val createdTime: String,
    val modifiedTime: String
)
