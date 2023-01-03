package com.starters.yeogida.data.remote.response.mypage

data class NotificationListResponse(
    val alarmList: List<NotificationData>
)

data class NotificationData(
    val nickname: String,
    val imgUrl: String,
    val alarmType: String,
    val followerId: Long,
    val tripId: Long,
    val placeId: Long,
    val commentId: Long,
    val text: String,
    val createdTime: String
)