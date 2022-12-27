package com.starters.yeogida.data.remote.response

import com.google.gson.annotations.SerializedName

data class BestTravelerResponse(
    val memberList: List<BestTravelerData>
)

data class BestTravelerData(
    @SerializedName("id") val memberId: Long,
    val nickname: String,
    val imgUrl: String,
    val heartCount: Int
)
