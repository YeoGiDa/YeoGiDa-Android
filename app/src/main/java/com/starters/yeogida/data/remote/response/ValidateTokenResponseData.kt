package com.starters.yeogida.data.remote.response

data class ValidateTokenResponseData(
    val newAccessToken: String,
    val refreshToken: String
)