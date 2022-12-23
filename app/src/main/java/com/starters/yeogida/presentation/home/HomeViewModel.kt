package com.starters.yeogida.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.Event

class HomeViewModel : ViewModel() {
    private val _openUserProfileEvent = MutableLiveData<Event<Long>>()
    val openUserProfileEvent: LiveData<Event<Long>> = _openUserProfileEvent

    fun onUserClicked(memberId: Long) {
        _openUserProfileEvent.value = Event(memberId)
    }
}