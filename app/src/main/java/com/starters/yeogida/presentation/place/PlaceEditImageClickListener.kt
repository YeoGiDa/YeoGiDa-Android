package com.starters.yeogida.presentation.place

import java.io.File

interface PlaceEditImageClickListener {
    fun deleteImage(imageFile: File)
    fun openImageScreen(imageFile: File)
}