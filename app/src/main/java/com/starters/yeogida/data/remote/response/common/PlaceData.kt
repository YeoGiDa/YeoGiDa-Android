package com.starters.yeogida.data.remote.response.common

data class PlaceData(
    val placeId: Long,
    val title: String,
    val star: Float,
    val imgUrl: String,
    val commentCount: Int,
    val tag: String
)