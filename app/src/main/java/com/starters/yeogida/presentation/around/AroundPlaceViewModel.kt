package com.starters.yeogida.presentation.around

import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.SingleLiveEvent

class AroundPlaceViewModel : ViewModel() {
    val openPlaceDetailEvent = SingleLiveEvent<Any>()

    fun onPlaceClicked() {
        openPlaceDetailEvent.call()
    }
}