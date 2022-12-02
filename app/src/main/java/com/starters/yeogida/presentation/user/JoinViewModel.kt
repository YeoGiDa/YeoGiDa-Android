package com.starters.yeogida.presentation.user

import androidx.lifecycle.ViewModel
import com.starters.yeogida.presentation.common.SingleLiveEvent
import java.io.File

class JoinViewModel : ViewModel() {
    val startGalleryEvent = SingleLiveEvent<Any>()

    fun onImageButtonClick() {
        startGalleryEvent.call()
    }

    fun signUpUser(imageFile: File) {
        /*val service = UserService.addUser(SignUpRequestData(

        ))*/
    }
}