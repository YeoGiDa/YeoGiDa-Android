package com.starters.yeogida.data.remote.response.mypage

data class MyProfileResponse(
    val id: Long,
    val nickname: String,
    val imgUrl: String,
    val followerCount: Int,
    val followingCount: Int,
    val tripCount: Int,
    val heartCount: Int
)