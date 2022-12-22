package com.starters.yeogida.data.local

import com.starters.yeogida.presentation.common.RegionCategory

data class LikeTripData(
    val tripId: Long,
    val region: RegionCategory,
    val imageUrl: String,
    val title: String,
    val subTitle: String,
    val nickname: String,
    val isLike: Boolean,
    val likeCount: Int,
    val placeCount: Int,
)
