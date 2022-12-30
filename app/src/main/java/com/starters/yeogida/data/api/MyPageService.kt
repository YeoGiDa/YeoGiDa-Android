package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.mypage.MyPlaceResponse
import com.starters.yeogida.data.remote.response.mypage.MyProfileResponse
import com.starters.yeogida.data.remote.response.mypage.MyTripResponse
import com.starters.yeogida.data.remote.response.mypage.NotificationListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface MyPageService {
    // 알림 목록
    @GET("me/alarm")
    fun getNotificationList(
        @Header("Authorization") token: String
    ): Call<BaseResponse<NotificationListResponse>>

    // 내 프로필 정보
    @GET("members/my")
    suspend fun getMyProfile(
        @Header("Authorization") bearerToken: String
    ): Response<BaseResponse<MyProfileResponse>>

    // 내가 작성한 여행지
    @GET("trips/my")
    suspend fun getMyTrip(
        @Header("Authorization") bearerToken: String
    ): Response<BaseResponse<MyTripResponse>>

    // 내가 댓글남긴 장소 목록
    @GET("places/commented")
    suspend fun getMyCommentedPlace(
        @Header("Authorization") bearerToken: String
    ): Response<BaseResponse<MyPlaceResponse>>

    // 내가 작성한 여행지 검색
    @GET("trips/my/search")
    suspend fun searchMyTrip(
        @Header("Authorization") bearerToken: String,
        @Query("keyword") searchText: String
    ): Response<BaseResponse<MyTripResponse>>

    // 회원 정보 수정
    @Multipart
    @PUT("members/update")
    suspend fun changeMyProfile(
        @Header("Authorization") bearerToken: String,
        @Part imgUrl: MultipartBody.Part,
        @PartMap putNicknameRequest: HashMap<String, RequestBody>
    ): Response<BaseResponse<Any>>
}
