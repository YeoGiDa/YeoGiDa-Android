package com.starters.yeogida.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.Event

class SearchViewModel : ViewModel() {
    private val _popularKeywordClickedEvent = MutableLiveData<Event<String>>()
    val popularKeywordClickedEvent: LiveData<Event<String>> = _popularKeywordClickedEvent

    private val _openAroundPlaceEvent = MutableLiveData<Event<Long>>()
    val openAroundPlaceEvent: LiveData<Event<Long>> = _openAroundPlaceEvent

    fun onKeywordClicked(searchKeyword: String) {
        _popularKeywordClickedEvent.value = Event(searchKeyword)
    }

    fun openAroundPlace(tripId: Long) {
        _openAroundPlaceEvent.value = Event(tripId)
    }
}