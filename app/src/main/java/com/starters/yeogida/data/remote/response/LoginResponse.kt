package com.starters.yeogida.data.remote.response

data class LoginResponse(
    val memberId: Long,
    val accessToken: String,
    val refreshToken: String
)
