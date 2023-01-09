package com.starters.yeogida.data.remote.response.trip

data class PopularTripResponse(
    val rankList: List<RankTrip>
)

data class RankTrip(
    val searchKeyword: String,
    val score: Double
)
