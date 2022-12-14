package com.starters.yeogida.util

import android.text.TextUtils
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <ResponseType> Call<ResponseType>.customEnqueue(
    onSuccess: (ResponseType) -> Unit,
    onError: (Response<ResponseType>) -> Unit = {},
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
            response.body()?.let {
                onSuccess(it)
            } ?: onError(response)

            Log.d("network", response.message())
            Log.d("network", response.code().toString())
        }
    })
}
