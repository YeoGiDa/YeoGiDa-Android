package com.starters.yeogida.data.local

data class UserProfileData(
    val memberId: Long,
    val nickname: String,
    val profileImgUrl: String,
    val followerCount: Int,
    val followingCount: Int,
    val isFollow: Boolean,
    val tripCount: Int,
    val tripLikeCount: Int,

    )
