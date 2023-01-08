package com.starters.yeogida.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.Event

class SearchViewModel : ViewModel() {
    private val _popularKeywordClickedEvent = MutableLiveData<Event<String>>()
    val popularKeywordClickedEvent: LiveData<Event<String>> = _popularKeywordClickedEvent

    fun onKeywordClicked(searchKeyword: String) {
        _popularKeywordClickedEvent.value = Event(searchKeyword)
    }
}