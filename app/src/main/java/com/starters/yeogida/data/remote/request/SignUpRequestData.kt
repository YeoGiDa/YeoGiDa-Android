package com.starters.yeogida.data.remote.request

data class SignUpRequestData(
    val email: String,
    val kakaoId: String,
    val nickname: String,
    val imgUrl: String
)
