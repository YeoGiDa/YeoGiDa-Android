package com.starters.yeogida.data.remote.response.mypage

data class MyTripResponse(
    val tripList: List<MyTrip>
)

data class MyTrip(
    val tripId: Long,
    val memberId: Long,
    val nickname: String,
    val title: String,
    val subTitle: String,
    val imgUrl: String,
    val heartCount: Int,
    val placeCount: Int
)
