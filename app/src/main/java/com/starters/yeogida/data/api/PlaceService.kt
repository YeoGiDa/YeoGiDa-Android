package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.request.place.CommentRequest
import com.starters.yeogida.data.remote.response.AscCommentsResponse
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.place.AddCommentResponse
import retrofit2.Call
import retrofit2.http.*

interface PlaceService {
    // 댓글 목록 (작성순)
    @GET("{placeId}/comments/idAsc")
    fun getAscComments(
        @Path("placeId") placeId: Long
    ): Call<BaseResponse<AscCommentsResponse>>

    // 댓글 작성
    @POST("{placeId}/comments")
    fun postComment(
        @Header("Authorization") token: String,
        @Path("placeId") placeId: Long,
        @Body body: CommentRequest
    ): Call<BaseResponse<AddCommentResponse>>
}