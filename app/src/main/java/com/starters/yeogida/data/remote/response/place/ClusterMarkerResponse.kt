package com.starters.yeogida.data.remote.response.place

data class ClusterMarkerResponse(
    val placeList: List<ClusterMarkerData>
)

data class ClusterMarkerData(
    val tripId: Long,
    val placeId: Long,
    val imgUrl: String,
    val nickname: String,
    val star: Float
)
