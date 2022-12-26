package com.starters.yeogida.data.remote.response.follow

data class FollowerUserResponse(
    val followerList: List<FollowUserData>
)

data class FollowingUserResponse(
    val followingList: List<FollowUserData>
)

data class FollowUserData(
    val memberId: Long,
    val nickname: String,
    val imgUrl: String
)