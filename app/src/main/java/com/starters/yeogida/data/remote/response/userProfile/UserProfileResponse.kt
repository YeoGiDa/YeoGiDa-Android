package com.starters.yeogida.data.remote.response.userProfile

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    val memberId: Long,
    val nickname: String,
    @SerializedName("imgUrl") val profileImgUrl: String,
    val followerCount: Int,
    val followingCount: Int,
    val tripCount: Int,
    @SerializedName("heartCount") val tripLikeCount: Int,
    val isFollow: Boolean
)
