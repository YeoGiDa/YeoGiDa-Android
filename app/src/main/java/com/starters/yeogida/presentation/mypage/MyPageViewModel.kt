package com.starters.yeogida.presentation.mypage

import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.SingleLiveEvent

class MyPageViewModel : ViewModel() {
    val logOutEvent = SingleLiveEvent<Any>()
    val withDrawDialogEvent = SingleLiveEvent<Any>()

    fun userLogout() {
        logOutEvent.call()
    }

    fun userWithDrawDialog() {
        withDrawDialogEvent.call()
    }
}
