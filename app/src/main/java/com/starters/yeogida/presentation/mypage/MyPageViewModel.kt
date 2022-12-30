package com.starters.yeogida.presentation.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.Event
import com.starters.yeogida.presentation.common.SingleLiveEvent

class MyPageViewModel : ViewModel() {

    private val _openAroundPlaceEvent = MutableLiveData<Event<Long>>()
    val openAroundPlaceEvent: LiveData<Event<Long>> = _openAroundPlaceEvent

    val openGalleryEvent = SingleLiveEvent<Any>()

    fun onTripClicked(tripId: Long) {
        _openAroundPlaceEvent.value = Event(tripId)
    }

    fun onImageButtonClicked() {
        openGalleryEvent.call()
    }
}
