package com.starters.yeogida.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.local.TripData
import com.starters.yeogida.databinding.ItemTripBinding

class TripAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val tripList = mutableListOf<TripData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TripViewHolder) {
            holder.onBind(tripList[position])
        }
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    class TripViewHolder(val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: TripData) {
            binding.trip = data
        }
    }
}
