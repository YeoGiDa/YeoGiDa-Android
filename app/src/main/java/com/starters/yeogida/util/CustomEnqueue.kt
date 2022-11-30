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
    onSuccess: ((Response<ResponseType>)) -> Unit,
    onError: (BaseResponse<ResponseType>) -> Unit,
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
                onSuccess.invoke(response)
                return
            }
            val errorBody = response.errorBody()?.string() ?: return
            val errorResponse = createResponseErrorBody(errorBody)
            onError.invoke(errorResponse)
        }

        private fun createResponseErrorBody(errorBody: String): BaseResponse<ResponseType> {
            val gson = GsonBuilder().create()
            val responseType = object : TypeToken<BaseResponse<ResponseType>>() {}.type
            return gson.fromJson(errorBody, responseType)
        }
    })
}
