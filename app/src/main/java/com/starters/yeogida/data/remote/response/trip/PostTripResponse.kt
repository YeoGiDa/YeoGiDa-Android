package com.starters.yeogida.data.remote.response.trip

data class PostTripResponse(
    val id: Long,
    val region: String,
    val title: String,
    val subTitle: String,
    val imgUrl: String,
    val createdTime: String,
    val modifiedTime: String
)
