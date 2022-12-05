package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.common.TokenData
import com.starters.yeogida.data.remote.request.LoginRequestData
import com.starters.yeogida.data.remote.request.SignUpRequestData
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.SignUpResponseData
import com.starters.yeogida.data.remote.response.ValidateTokenResponseData
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @POST("members/join")
    suspend fun addUser(
        @Body signUpRequestData: SignUpRequestData
    ): Response<BaseResponse<SignUpResponseData>>

    @POST("members/login")
    suspend fun postLogin(
        @Body loginRequestData: LoginRequestData
    ): Response<BaseResponse<TokenData>>

    @POST("token/validate")
    suspend fun validateToken(
        @Body tokenData: TokenData
    ): Response<BaseResponse<ValidateTokenResponseData>>

    @DELETE("members/delete")
    suspend fun withDrawUser(
        @Header("Authorization") accessToken: String
    ): Response<BaseResponse<Any>>
}