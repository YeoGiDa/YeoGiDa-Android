package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.trip.LikeTripResponse
import com.starters.yeogida.data.remote.response.trip.PostTripResponse
import com.starters.yeogida.data.remote.response.trip.TripInfoResponse
import com.starters.yeogida.data.remote.response.trip.TripLikeUserResponse
import com.starters.yeogida.data.remote.response.userProfile.UserProfileTripResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface TripService {
    // 여행지 추가
    @Multipart
    @POST("trips/save")
    fun postTrip(
        @Header("Authorization") token: String,
        @Part imgUrl: MultipartBody.Part,
        @PartMap postTripRequest: HashMap<String, RequestBody>
    ): Call<BaseResponse<PostTripResponse>>

    // 장소 목록 - 여행지 조회
    @GET("{tripId}/places/tripInfo")
    fun getTripInfo(
        @Header("Authorization") bearerToken: String,
        @Path("tripId") tripId: Long
    ): Call<BaseResponse<TripInfoResponse>>

    // 여행지 좋아요 누르기
    @POST("trips/{tripId}/heart")
    suspend fun postTripHeart(
        @Header("Authorization") bearerToken: String,
        @Path("tripId") tripId: Long,
    ): Response<BaseResponse<Any>>

    // 좋아요 취소하기
    @DELETE("trips/{tripId}/heart")
    suspend fun deleteTripHeart(
        @Header("Authorization") bearerToken: String,
        @Path("tripId") tripId: Long,
    ): Response<BaseResponse<Any>>

    // 여기 좋아 - 좋아하는 여행지 조회
    @GET("trips/my/heart")
    suspend fun getLikeTrip(
        @Header("Authorization") bearerToken: String,
    ): Response<BaseResponse<LikeTripResponse>>

    // 여기 좋아 - 지역 별 여행지 조회
    @GET("trips/my/heart/{region}")
    suspend fun getRegionLikeTrip(
        @Header("Authorization") bearerToken: String,
        @Path("region") region: String
    ): Response<BaseResponse<LikeTripResponse>>

    // 여기 좋아 - 전체 검색
    @GET("trips/my/heart/search")
    suspend fun searchLikeTrip(
        @Header("Authorization") bearerToken: String,
        @Query("keyword") keyword: String
    ): Response<BaseResponse<LikeTripResponse>>

    // 유저 상세 여행지 목록
    @GET("trips/member/{memberId}/{region}")
    suspend fun getUserTripList(
        @Path("memberId") memberId: Long,
        @Path("region") region: String,
        @Query("condition") condition: String
    ): Response<BaseResponse<UserProfileTripResponse>>

    // 여행지에 좋아요를 누른 유저 목록
    @GET("trips/{tripId}/heartMembers")
    suspend fun getTripLikeUserList(
        @Path("tripId") tripId: Long
    ): Response<BaseResponse<TripLikeUserResponse>>

    // 여행지 삭제
    @DELETE("trips/{tripId}")
    fun deleteTrip(
        @Header("Authorization") token: String,
        @Path("tripId") tripId: Long
    ): Call<BaseResponse<Any>>
}