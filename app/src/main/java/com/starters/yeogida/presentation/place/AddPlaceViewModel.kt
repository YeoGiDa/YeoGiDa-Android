package com.starters.yeogida.presentation.place

import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.SingleLiveEvent

class AddPlaceViewModel : ViewModel() {
    val openGalleryEvent = SingleLiveEvent<Any>()

    fun onAddPhotoButtonClicked() {
        openGalleryEvent.call()
    }
}