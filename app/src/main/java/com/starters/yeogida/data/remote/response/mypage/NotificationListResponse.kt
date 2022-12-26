package com.starters.yeogida.data.remote.response.mypage

data class NotificationListResponse(
    val alarmList: List<NotificationData>
)

data class NotificationData(
    val nickname: String,
    val imgUrl: String,
    val alarmType: String,
    val targetId: Long,
    val text: String,
    val createdTime: String
)