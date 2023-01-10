package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.trip.PopularTripResponse
import com.starters.yeogida.data.remote.response.trip.TripSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    // 인기 검색어 가져오기
    @GET("trips/search/ranking")
    suspend fun getPopularTrip(): Response<BaseResponse<PopularTripResponse>>

    // 검색 결과 가져오기 (정렬, 검색어)
    @GET("trips/search")
    suspend fun getSearchResult(
        @Query("keyword") searchKeyword: String,
        @Query("condition") condition: String
    ): Response<BaseResponse<TripSearchResponse>>
}