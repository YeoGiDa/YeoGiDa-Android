package com.starters.yeogida.util

import android.text.TextUtils
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.starters.yeogida.data.remote.response.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <ResponseType> Call<ResponseType>.customEnqueue(
    onSuccess: (ResponseType) -> Unit,
    onError: (ResponseType) -> Unit = {},
    onFail: () -> Unit = {}
) {
    this.enqueue(object : Callback<ResponseType> {
        override fun onFailure(call: Call<ResponseType>, t: Throwable) {
            Log.d("network", "${t.message} \n")
            Log.d("network", "${t.localizedMessage} \n")
            Log.d("network", TextUtils.join("\n", t.stackTrace))
            onFail()
        }

        override fun onResponse(call: Call<ResponseType>, response: Response<ResponseType>) {
            if (response.isSuccessful) {
                onSuccess?.invoke(response.body() ?: return)
                return
            }
            val errorBody = response.errorBody()?.string() ?: return
            val errorResponse = createErrorResponse(errorBody)
            onError?.invoke(errorResponse)

            Log.d("network", response.message())
            Log.d("network", response.code().toString())
        }

        private fun createErrorResponse(errorBody: String): ResponseType {
            val responseType = object : TypeToken<BaseResponse<Unit>>() {}.type
            return GsonBuilder().create()
                .fromJson(errorBody, responseType)
        }
    })
}
