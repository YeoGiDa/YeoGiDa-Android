package com.starters.yeogida.network

import com.starters.yeogida.data.api.HomeService
import com.starters.yeogida.data.api.PlaceService
import com.starters.yeogida.data.api.UserService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface YeogidaClient {
    companion object {
        private const val BASE_URL = "http://54.180.100.93:8080/api/v1/"

        val userService: UserService by lazy {
            retrofit.create(UserService::class.java)
        }

        val homeService: HomeService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            provideService(HomeService::class.java)
        }

        val placeService: PlaceService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            provideService(PlaceService::class.java)
        }

        // YS
        private val okHttpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                .build()
        }

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient) // 로그캣에서 패킷 내용을 모니터링 할 수 있음 (인터셉터)
                .build()
        }

        // HJ
        private fun <T> provideService(service: Class<T>): T = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideHttpLoggingClient())
            .build()
            .create(service)

        private fun provideHttpLoggingClient(): OkHttpClient =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }.let {
                OkHttpClient.Builder().addInterceptor(it).build()
            }
    }
}
