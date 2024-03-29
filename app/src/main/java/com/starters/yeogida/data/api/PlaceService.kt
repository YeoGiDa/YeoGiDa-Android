package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.request.place.CommentRequest
import com.starters.yeogida.data.remote.response.AscCommentsResponse
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.place.AddCommentResponse
import com.starters.yeogida.data.remote.response.place.PlaceDetailResponse
import com.starters.yeogida.data.remote.response.place.PlaceListResponse
import com.starters.yeogida.data.remote.response.place.PlaceMapListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    // 장소 지도로 보기
    @GET("{tripId}/places/inMap")
    fun getPlaceMapList(
        @Path("tripId") tripId: Long
    ): Call<BaseResponse<PlaceMapListResponse>>

    // 장소 상세
    @GET("places/{placeId}")
    suspend fun getPlaceDetail(
        @Path("placeId") placeId: Long
    ): Response<BaseResponse<PlaceDetailResponse>>

    // 댓글 목록 (작성순)
    @GET("{placeId}/comments/idAsc")
    fun getAscComments(
        @Path("placeId") placeId: Long
    ): Call<BaseResponse<AscCommentsResponse>>

    // 댓글 작성
    @POST("{placeId}/comments")
    fun postComment(
        @Path("placeId") placeId: Long,
        @Body body: CommentRequest
    ): Call<BaseResponse<AddCommentResponse>>

    // 댓글 삭제
    @DELETE("comments/{commentId}")
    fun deleteComment(
        @Path("commentId") commentId: Long
    ): Call<BaseResponse<Any>>

    // 장소 추가
    @Multipart
    @POST("{tripId}/places/save")
    suspend fun postPlace(
        @Path("tripId") tripId: Long,
        @PartMap postPlaceRequest: HashMap<String, RequestBody>,
        @Part imgUrls: List<MultipartBody.Part>
    ): Response<BaseResponse<PlaceDetailResponse>>

    // 장소 삭제
    @DELETE("places/{placeId}")
    fun deletePlace(
        @Path("placeId") placeId: Long
    ): Call<BaseResponse<Any>>

    // 장소 수정
    @Multipart
    @PUT("places/{placeId}")
    suspend fun editPlace(
        @Path("placeId") placeId: Long,
        @PartMap editPlaceRequest: HashMap<String, RequestBody>,
        @Part imgUrl: List<MultipartBody.Part>
    ): Response<BaseResponse<Any>>
}