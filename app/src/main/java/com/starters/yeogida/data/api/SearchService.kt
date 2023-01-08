package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.trip.PopularTripResponse
import retrofit2.Response
import retrofit2.http.GET

interface SearchService {

    // 인기 검색어 가져오기
    @GET("trips/search/ranking")
    suspend fun getPopularTrip(): Response<BaseResponse<PopularTripResponse>>

}