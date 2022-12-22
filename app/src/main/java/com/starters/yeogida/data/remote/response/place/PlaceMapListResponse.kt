package com.starters.yeogida.data.remote.response.place

data class PlaceMapListResponse(
    val meanLat: Double,
    val meanLng: Double,
    val placeList: List<PlaceMapList>
)

data class PlaceMapList(
    val placeId: Long,
    val title: String,
    val address: String,
    val star: Float,
    val commentCount: Int,
    val imgUrl: String,
    val tag: String,
    val latitude: Double,
    val longitude: Double
)
