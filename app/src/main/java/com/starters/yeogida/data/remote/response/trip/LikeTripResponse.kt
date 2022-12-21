package com.starters.yeogida.data.remote.response.trip

data class LikeTripResponse(
    val tripList: List<LikeTrip>
)

data class LikeTrip(
    val tripId: Long,
    val memberId: Long,
    val title: String,
    val subTitle: String,
    val imgUrl: String,
    val heartCount: Int,
    val placeCount: Int
)
