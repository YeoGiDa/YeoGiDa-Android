package com.starters.yeogida.network

import com.starters.yeogida.data.api.UserService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface ApiClient {
    companion object {
        private const val BASE_URL = "http://192.168.0.115:8080/api/v1/"

        private val okHttpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS)
                .writeTimeout(100,TimeUnit.SECONDS)
                .build()
        }

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient) // 로그캣에서 패킷 내용을 모니터링 할 수 있음 (인터셉터)
                .build()
        }

        val userService : UserService by lazy {
            retrofit.create(UserService::class.java)
        }
    }
}