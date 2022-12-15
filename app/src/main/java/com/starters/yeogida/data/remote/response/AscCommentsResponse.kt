package com.starters.yeogida.data.remote.response

data class AscCommentsResponse(
    val commentCounts: Int,
    val commentList: List<CommentData>
)

data class CommentData(
    val commentId: Long,
    val memberId: Long,
    val imgUrl: String,
    val nickName: String,
    val createdTime: String,
    val content: String
)
