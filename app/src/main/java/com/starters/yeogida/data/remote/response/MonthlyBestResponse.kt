package com.starters.yeogida.data.remote.response

data class MonthlyBestResponse(
    val tripList: List<TripListData>
)

data class TripListData(
    val tripId: Long,
    val memberId: Long,
    val title: String,
    val imgUrl: String,
    val nickname: String
)
