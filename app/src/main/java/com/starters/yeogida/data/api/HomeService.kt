package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.BestTravelerResponse
import com.starters.yeogida.data.remote.response.HomeTripResponseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeService {
    // 월간 베스트 여행지
    @GET("trips/monthly-best/basic")
    fun getMonthlyBest(): Call<BaseResponse<HomeTripResponseResponse>>

    // best traveler
    @GET("members/best-traveler/basic")
    fun getBestTraveler(): Call<BaseResponse<BestTravelerResponse>>

    // 팔로잉의 최근 게시글
    @GET("trips/follow/basic")
    fun getRecentTrip(): Call<BaseResponse<HomeTripResponseResponse>>
}