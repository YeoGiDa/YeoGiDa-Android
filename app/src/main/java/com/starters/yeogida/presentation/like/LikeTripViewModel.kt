package com.starters.yeogida.presentation.like

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.SingleLiveEvent

class LikeTripViewModel : ViewModel() {

    val openAroundPlaceEvent = SingleLiveEvent<Any>()

    val tripId = MutableLiveData<Long>()

    fun onTripClicked(clickTripId: Long) {
        openAroundPlaceEvent.call()
        tripId.value = clickTripId
    }
}