package com.starters.yeogida.data.remote.response

data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)