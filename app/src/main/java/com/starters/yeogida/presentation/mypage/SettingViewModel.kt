package com.starters.yeogida.presentation.mypage

import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.SingleLiveEvent

class SettingViewModel : ViewModel() {
    val logOutEvent = SingleLiveEvent<Any>()
    val withDrawDialogEvent = SingleLiveEvent<Any>()

    fun logoutUser() {
        logOutEvent.call()
    }

    fun showWithDrawDialog() {
        withDrawDialogEvent.call()
    }
}
