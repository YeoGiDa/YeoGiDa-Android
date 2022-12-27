package com.starters.yeogida.data.remote.response.trip

import com.google.gson.annotations.SerializedName

data class TripInfoResponse(
    val memberId: Long,
    @SerializedName("member_imgUrl") val profileImgUrl: String,
    val nickname: String,
    @SerializedName("trip_like_check") val isLike: Boolean,
    val title: String,
    val subTitle: String,
    val imgUrl: String,
    val heartCount: Int,
    val placeCount: Int
)
