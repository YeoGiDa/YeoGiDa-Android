package com.starters.yeogida.data.remote.response.place

data class PlaceListResponse(
    val memberId: Long,
    val placeList: List<PlaceData>
)

data class PlaceData(
    val placeId: Long,
    val title: String,
    val star: Float,
    val imgUrl: String,
    val commentCount: Int,
    val tag: String
)
