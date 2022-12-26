package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.follow.FollowerUserResponse
import com.starters.yeogida.data.remote.response.follow.FollowingUserResponse
import retrofit2.Response
import retrofit2.http.*

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

    // 팔로잉 추가
    @POST("follow/{toMemberId}")
    suspend fun addFollowing(
        @Header("Authorization") bearerToken: String,
        @Path("toMemberId") memberId: Long
    ): Response<BaseResponse<Boolean>>

    // 팔로잉 삭제
    @DELETE("follow/{toMemberId}/following")
    suspend fun deleteFollowing(
        @Header("Authorization") bearerToken: String,
        @Path("toMemberId") memberId: Long
    ): Response<BaseResponse<Boolean>>

    // 팔로워 삭제
    @DELETE("follow/{fromMemberId}/follower")
    suspend fun deleteFollower(
        @Header("Authorization") bearerToken: String,
        @Path("fromMemberId") memberId: Long
    ): Response<BaseResponse<Boolean>>

    // 팔로워 검색
    @GET("follow/search/follower")
    suspend fun searchFollower(
        @Header("Authorization") bearerToken: String,
        @Query("nickname") nickname: String
    ): Response<BaseResponse<FollowerUserResponse>>

    // 팔로잉 검색
    @GET("follow/search/following")
    suspend fun searchFollowing(
        @Header("Authorization") bearerToken: String,
        @Query("nickname") nickname: String
    ): Response<BaseResponse<FollowingUserResponse>>
}