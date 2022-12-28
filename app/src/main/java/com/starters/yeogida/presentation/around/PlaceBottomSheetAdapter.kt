package com.starters.yeogida.presentation.around

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.place.ClusterMarkerData
import com.starters.yeogida.databinding.ItemBottomSheetPlaceBinding

class PlaceBottomSheetAdapter(val itemClick: (Long, Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val placeList = mutableListOf<ClusterMarkerData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemBottomSheetPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BottomSheetPlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BottomSheetPlaceViewHolder) {
            holder.onBind(placeList[position])
        }
        holder.itemView.setOnClickListener {
            itemClick(placeList[position].tripId, placeList[position].placeId)
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    class BottomSheetPlaceViewHolder(val binding: ItemBottomSheetPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: ClusterMarkerData) {
            binding.place = data
        }
    }
}
