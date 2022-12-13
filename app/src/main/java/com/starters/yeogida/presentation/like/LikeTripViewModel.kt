package com.starters.yeogida.presentation.like

import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.SingleLiveEvent

class LikeTripViewModel : ViewModel() {

    val openAroundPlaceEvent = SingleLiveEvent<Any>()

    fun onTripClicked() {
        openAroundPlaceEvent.call()
    }
}