package com.starters.yeogida.data.remote.response.place

import com.starters.yeogida.data.remote.response.common.PlaceData

data class PlaceListResponse(
    val memberId: Long,
    val placeList: List<PlaceData>
)