package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.place.ClusterPlaceResponse
import retrofit2.Call
import retrofit2.http.*

interface AroundService {
    // 장소 둘러보기 메인
    @GET("places/inMap")
    fun getClusterList(): Call<BaseResponse<ClusterPlaceResponse>>
}
