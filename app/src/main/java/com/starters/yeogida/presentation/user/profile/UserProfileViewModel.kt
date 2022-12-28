package com.starters.yeogida.presentation.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.Event

class UserProfileViewModel : ViewModel() {
    private val _followUserEvent = MutableLiveData<Event<Long>>()
    val followUserEvent: LiveData<Event<Long>> = _followUserEvent

    private val _openAroundPlaceEvent = MutableLiveData<Event<Long>>()
    val openAroundPlaceEvent: LiveData<Event<Long>> = _openAroundPlaceEvent

    fun onFollowBtnClicked(memberId: Long) {
        _followUserEvent.value = Event(memberId)
    }

    fun onTripClicked(tripId: Long) {
        _openAroundPlaceEvent.value = Event(tripId)
    }
}