package com.starters.yeogida.data.api

import com.starters.yeogida.data.remote.response.AscCommentsResponse
import com.starters.yeogida.data.remote.response.BaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PlaceService {
    // 댓글 목록 (작성순)
    @GET("{placeId}/comments/idAsc")
    fun getAscComments(
        @Path("placeId") placeId: Long
    ): Call<BaseResponse<AscCommentsResponse>>
}