package com.starters.yeogida.presentation.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.Event

class UserProfileViewModel : ViewModel() {
    private val _followUserEvent = MutableLiveData<Event<Long>>()
    val followUserEvent: LiveData<Event<Long>> = _followUserEvent

    fun onFollowBtnClicked(memberId: Long) {
        _followUserEvent.value = Event(memberId)
    }
}