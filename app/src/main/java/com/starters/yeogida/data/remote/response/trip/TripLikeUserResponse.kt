package com.starters.yeogida.data.remote.response.trip

data class TripLikeUserResponse(
    val memberList: List<TripLikeUserData>
)

data class TripLikeUserData(
    val memberId: Long,
    val nickname: String,
    val imgUrl: String
)
