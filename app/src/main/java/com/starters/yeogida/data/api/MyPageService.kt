package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.mypage.NotificationListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MyPageService {
    // 알림 목록
    @GET("me/alarm")
    fun getNotificationList(
        @Header("Authorization") token: String
    ): Call<BaseResponse<NotificationListResponse>>
}
