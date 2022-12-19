package com.starters.yeogida.data.remote.response.trip

data class TripInfoResponse(
    val title: String,
    val subTitle: String,
    val imgUrl: String,
    val heartCount: Int,
    val placeCount: Int
)
