package com.starters.yeogida.data.remote.response.place

data class PlaceDetailResponse(
    val tripId: Long,
    val placeId: Long,
    val memberId: Long,
    val title: String,
    val address: String,
    val star: Float,
    val content: String,
    val placeImgs: List<PlaceImg>,
    val createdTime: String,
    val tag: String
)

data class PlaceImg(
    val id: Long,
    val imgUrl: String
)
