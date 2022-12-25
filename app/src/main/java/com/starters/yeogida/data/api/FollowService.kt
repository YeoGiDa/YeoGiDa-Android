package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.follow.FollowerUserResponse
import com.starters.yeogida.data.remote.response.follow.FollowingUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface FollowService {
    // 팔로워 목록 조회
    @GET("follow/follower")
    suspend fun getFollowerUser(
        @Header("Authorization") bearerToken: String
    ): Response<BaseResponse<FollowerUserResponse>>

    // 팔로잉 목록 조회
    @GET("follow/following")
    suspend fun getFollowingUser(
        @Header("Authorization") bearerToken: String
    ): Response<BaseResponse<FollowingUserResponse>>
}