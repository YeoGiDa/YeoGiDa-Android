package com.starters.yeogida.presentation.like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.Event
import com.starters.yeogida.presentation.common.SingleLiveEvent

class LikeTripViewModel : ViewModel() {

    private val _openAroundPlaceEvent = MutableLiveData<Event<Long>>()
    val openAroundPlaceEvent: LiveData<Event<Long>> = _openAroundPlaceEvent

    val moveTopEvent = SingleLiveEvent<Any>()

    fun onTripClicked(tripId: Long) {
        _openAroundPlaceEvent.value = Event(tripId)
    }

    fun onMoveTopClicked() {
        moveTopEvent.call()
    }
}