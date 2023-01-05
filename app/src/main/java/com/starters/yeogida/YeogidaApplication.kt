package com.starters.yeogida

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk

class YeogidaApplication : Application() {
    private lateinit var dataStore: YeogidaDataStore

    companion object {
        private lateinit var yeogidaApplication: YeogidaApplication
        // var instance: YeogidaApplication? = null

        fun getInstance(): YeogidaApplication = yeogidaApplication
        fun context(): Context {
            return getInstance().applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        yeogidaApplication = this
        KakaoSdk.init(this, getString(R.string.kakao_native_key))
        dataStore = YeogidaDataStore(this)
    }

    fun getDataStore(): YeogidaDataStore = dataStore
}
