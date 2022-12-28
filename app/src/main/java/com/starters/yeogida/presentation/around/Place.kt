package com.starters.yeogida.presentation.around

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class Place(
    val id: Long,
    val latLng: LatLng,
    val name: String,
    val address: String
) : ClusterItem {
    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String? {
        return ""
    }

    override fun getSnippet(): String? {
        return ""
    }

}