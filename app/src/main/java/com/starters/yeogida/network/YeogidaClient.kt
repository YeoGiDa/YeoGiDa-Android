package com.starters.yeogida.network

import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.api.*
import com.starters.yeogida.data.interceptor.AuthInterceptor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object YeogidaClient {
    private const val BASE_URL = "https://api.yeogida.site/api/v1/"

    val userService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        provideService(UserService::class.java)
    }

    val homeService: HomeService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        provideService(HomeService::class.java)
    }

    val aroundService: AroundService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        provideService(AroundService::class.java)
    }

    val placeService: PlaceService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        provideService(PlaceService::class.java)
    }

    val tripService: TripService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        provideService(TripService::class.java)
    }

    val myPageService: MyPageService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        provideService(MyPageService::class.java)
    }

    val followService: FollowService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        provideService(FollowService::class.java)
    }

    val searchService: SearchService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        provideService(SearchService::class.java)
    }

    // HJ
    private fun <T> provideService(service: Class<T>): T = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(provideHttpLoggingClient())
        .build()
        .create(service)

    private fun provideHttpLoggingClient(): OkHttpClient =
        OkHttpClient.Builder()
            .run {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                addInterceptor(provideInterceptor())
                addInterceptor(AuthInterceptor(BASE_URL))
                build()
            }

    private fun provideInterceptor() =
        Interceptor { chain ->
            with(chain) {
                runBlocking {
                    val token =
                        YeogidaApplication.getInstance().getDataStore().userBearerToken.first()
                    val newRequest = request().newBuilder()
                        .addHeader(
                            "Authorization",
                            token
                        )
                        .build()
                    proceed(newRequest)
                }
            }
        }
}
