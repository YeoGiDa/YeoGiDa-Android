package com.starters.yeogida.data.remote.response.mypage

import com.google.gson.annotations.SerializedName

data class MyProfileResponse(
    @SerializedName("id") val memberId: Long,
    val nickname: String,
    val imgUrl: String,
    val followerCount: Int,
    val followingCount: Int,
    val tripCount: Int,
    val heartCount: Int
)