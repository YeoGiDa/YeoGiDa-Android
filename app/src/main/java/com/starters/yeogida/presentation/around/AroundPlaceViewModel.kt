package com.starters.yeogida.presentation.around

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.Event

class AroundPlaceViewModel : ViewModel() {

    private val _openPlaceDetailEvent = MutableLiveData<Event<Long>>()
    val openPlaceDetailEvent: LiveData<Event<Long>> = _openPlaceDetailEvent

    private val _openUserProfileEvent = MutableLiveData<Event<Long>>()
    val openUserProfileEvent: LiveData<Event<Long>> = _openUserProfileEvent

    private val _openTripLikeListEvent = MutableLiveData<Event<Long>>()
    val openTripLikeListEvent: LiveData<Event<Long>> = _openTripLikeListEvent

    private val _likeTripEvent = MutableLiveData<Event<Long>>()
    val likeTripEvent: LiveData<Event<Long>> = _likeTripEvent

    fun onPlaceClicked(placeId: Long) {
        _openPlaceDetailEvent.value = Event(placeId)
    }

    fun onUserProfileClicked(memberId: Long) {
        _openUserProfileEvent.value = Event(memberId)
    }

    fun onLikeCountClicked(tripId: Long) {
        _openTripLikeListEvent.value = Event(tripId)
    }

    fun onLikeBtnClicked(tripId: Long) {
        _likeTripEvent.value = Event(tripId)
    }
}