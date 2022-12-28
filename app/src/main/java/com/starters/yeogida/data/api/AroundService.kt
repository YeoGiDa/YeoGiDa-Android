package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.place.ClusterMarkerResponse
import com.starters.yeogida.data.remote.response.place.ClusterPlaceResponse
import retrofit2.Call
import retrofit2.http.*

interface AroundService {
    // 둘러보기 메인
    @GET("places/inMap")
    fun getClusterList(): Call<BaseResponse<ClusterPlaceResponse>>

    // 둘러보기 마커 데이터
    @GET("places/marker")
    fun getClusterMarkerData(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Call<BaseResponse<ClusterMarkerResponse>>
}
