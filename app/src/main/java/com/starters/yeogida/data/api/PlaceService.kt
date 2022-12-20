package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.request.place.CommentRequest
import com.starters.yeogida.data.remote.response.AscCommentsResponse
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.place.AddCommentResponse
import com.starters.yeogida.data.remote.response.place.PlaceDetailResponse
import com.starters.yeogida.data.remote.response.place.PlaceListResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface PlaceService {
    // 장소 목록
    @GET("{tripId}/places/{tag}")
    fun getPlaceTagList(
        @Path("tripId") tripId: Long,
        @Path("tag") tag: String,
        @Query("condition") condition: String
    ): Call<BaseResponse<PlaceListResponse>>

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

    // 댓글 삭제
    @DELETE("comments/{commentId}")
    fun deleteComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long
    ): Call<BaseResponse<Any>>

    @GET("places/{placeId}")
    suspend fun getPlaceDetail(
        @Path("placeId") placeId: Long
    ): Response<BaseResponse<PlaceDetailResponse>>
}