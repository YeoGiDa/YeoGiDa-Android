package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.request.SignUpRequestData
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.CheckMemberResponseData
import com.starters.yeogida.data.remote.response.SignUpResponseData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    @GET("members/checkMember")
    suspend fun checkMember(@Query("email") email : String) : Response<BaseResponse<CheckMemberResponseData>>

    @POST("members/join")
    suspend fun addUser(@Body signUpRequestData: SignUpRequestData) : BaseResponse<SignUpResponseData>
}