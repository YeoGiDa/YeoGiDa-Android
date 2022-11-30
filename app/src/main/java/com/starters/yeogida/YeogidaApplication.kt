package com.starters.yeogida

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class YeogidaApplication : Application() {

    private lateinit var dataStore : DataStoreModule

    companion object {
         private lateinit var yeogidaApplication: YeogidaApplication
         fun getInstance() : YeogidaApplication = yeogidaApplication
    }

    override fun onCreate() {
        super.onCreate()
        yeogidaApplication = this
        KakaoSdk.init(this, getString(R.string.kakao_native_key))
        dataStore = DataStoreModule(this)
    }

    fun getDataStore() : DataStoreModule = dataStore
}