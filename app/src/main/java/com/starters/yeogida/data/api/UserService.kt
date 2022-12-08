package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.common.TokenData
import com.starters.yeogida.data.remote.request.LoginRequestData
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.SignUpResponseData
import com.starters.yeogida.data.remote.response.ValidateTokenResponseData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @Multipart
    @POST("members/join")
    suspend fun addUser(
        @Part imgUrl: MultipartBody.Part?,
        @PartMap memberJoinRequest: HashMap<String, RequestBody>
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
        @Header("Authorization") bearerToken: String
    ): Response<BaseResponse<Any>>
}