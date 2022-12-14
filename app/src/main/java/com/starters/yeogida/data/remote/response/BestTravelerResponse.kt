package com.starters.yeogida.data.remote.response

data class BestTravelerResponse(
    val memberList: List<BestTravelerData>
)

data class BestTravelerData(
    val id: Long,
    val nickname: String,
    val imgUrl: String,
    val heartCount: Int
)
