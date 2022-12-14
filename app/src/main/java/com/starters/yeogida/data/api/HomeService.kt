package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.BestTravelerResponse
import com.starters.yeogida.data.remote.response.MonthlyBestResponse
import retrofit2.Call
import retrofit2.http.GET

interface HomeService {
    @GET("trips/monthly-best/basic")
    fun getMonthlyBest(): Call<BaseResponse<MonthlyBestResponse>>

    @GET("members/best-traveler/basic")
    fun getBestTraveler(): Call<BaseResponse<BestTravelerResponse>>
}