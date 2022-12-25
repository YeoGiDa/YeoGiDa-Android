package com.starters.yeogida.data.remote.response.follow

import com.google.gson.annotations.SerializedName

data class FollowerUserResponse(
    val followerList: List<FollowUserData>
)

data class FollowingUserResponse(
    val followingList: List<FollowUserData>
)

data class FollowUserData(
    @SerializedName("id") val memberId: Long,
    @SerializedName("imgUrl") val userProfileImageUrl: String,
    @SerializedName("nickname") val userNickname: String
)