package com.starters.yeogida.presentation.place

import android.net.Uri

interface PlaceImageClickListener {
    fun deleteImage(imageUri: Uri)
    fun openImageScreen(imageUri: Uri)
}