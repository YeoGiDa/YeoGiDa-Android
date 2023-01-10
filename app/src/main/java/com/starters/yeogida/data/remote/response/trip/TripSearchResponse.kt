package com.starters.yeogida.data.remote.response.trip

data class TripSearchResponse(
    val tripList: List<SearchTrip>
)

data class SearchTrip(
    val tripId: Long,
    val memberId: Long,
    val nickname: String,
    val title: String,
    val subTitle: String,
    val imgUrl: String,
    val heartCount: Int,
    val placeCount: Int
)
