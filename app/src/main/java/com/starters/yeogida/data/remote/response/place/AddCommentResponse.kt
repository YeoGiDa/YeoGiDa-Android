package com.starters.yeogida.data.remote.response.place

data class AddCommentResponse(
    val commentId: Long,
    val memberId: Long,
    val imgUrl: String,
    val nickName: String,
    val createdTime: String,
    val content: String
)
