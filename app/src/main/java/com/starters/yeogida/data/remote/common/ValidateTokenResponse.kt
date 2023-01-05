package com.starters.yeogida.data.remote.common

data class ValidateTokenResponse(
    val code: Int,
    val message: String,
    val data: Data,
) {
    data class Data(
        val newAccessToken: String,
        val refreshToken: String
    )
}
