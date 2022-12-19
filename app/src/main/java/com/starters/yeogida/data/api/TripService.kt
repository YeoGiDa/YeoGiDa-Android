package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.trip.PostTripResponse
import com.starters.yeogida.data.remote.response.trip.TripInfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface TripService {
    // 여행지 추가
    @Multipart
    @POST("trips/save")
    fun postTrip(
        @Header("Authorization") token: String,
        @Part imgUrl: MultipartBody.Part,
        @PartMap postTripRequest: HashMap<String, RequestBody>
    ): Call<BaseResponse<PostTripResponse>>

    // 장소 목록 - 여행지 조회
    @GET("{tripId}/places/tripInfo")
    fun getTripInfo(
        @Path("tripId") tripId: Long
    ): Call<BaseResponse<TripInfoResponse>>
}