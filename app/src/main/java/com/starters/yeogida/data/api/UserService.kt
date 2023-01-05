package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.common.TokenData
import com.starters.yeogida.data.remote.request.LoginRequestData
import com.starters.yeogida.data.remote.request.ReportRequest
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.LoginResponse
import com.starters.yeogida.data.remote.response.SignUpResponseData
import com.starters.yeogida.data.remote.response.ValidateTokenResponseData
import com.starters.yeogida.data.remote.response.userProfile.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    // 회원가입
    @Multipart
    @POST("members/join")
    suspend fun addUser(
        @Part imgUrl: MultipartBody.Part,
        @PartMap memberJoinRequest: HashMap<String, RequestBody>
    ): Response<BaseResponse<SignUpResponseData>>

    // 로그인
    @POST("members/login")
    suspend fun postLogin(
        @Body loginRequestData: LoginRequestData
    ): Response<BaseResponse<LoginResponse>>

    // 토큰 유효성 검사
    @POST("token/validate")
    suspend fun validateToken(
        @Body tokenData: TokenData
    ): Response<BaseResponse<ValidateTokenResponseData>>

    // 회원탈퇴
    @DELETE("members/delete")
    suspend fun withDrawUser(): Response<BaseResponse<Any>>

    // 유저 상세 불러오기
    @GET("follow/{findMemberId}/detail")
    suspend fun getUserProfile(
        @Path("findMemberId") memberId: Long
    ): Response<BaseResponse<UserProfileResponse>>

    // 신고하기
    @POST("report")
    fun postReport(
        @Body body: ReportRequest
    ): Call<BaseResponse<Any>>
}