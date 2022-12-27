package com.starters.yeogida.data.remote.response.place

data class ClusterPlaceResponse(
    val placeList: List<ClusterPlaceData>
)

data class ClusterPlaceData(
    val placeId: Long,
    val title: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
