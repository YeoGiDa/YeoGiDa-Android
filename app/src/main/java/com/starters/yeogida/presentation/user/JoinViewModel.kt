package com.starters.yeogida.presentation.user

import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.SingleLiveEvent

class JoinViewModel : ViewModel() {
    val startGalleryEvent = SingleLiveEvent<Any>()

    fun onImageButtonClick() {
        startGalleryEvent.call()
    }
}