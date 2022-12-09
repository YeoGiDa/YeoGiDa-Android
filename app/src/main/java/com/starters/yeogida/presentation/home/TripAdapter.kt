package com.starters.yeogida.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.local.TripData
import com.starters.yeogida.databinding.ItemHomeTripBinding

class TripAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val tripList = mutableListOf<TripData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemHomeTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeTripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomeTripViewHolder) {
            holder.onBind(tripList[position])
        }
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    class HomeTripViewHolder(val binding: ItemHomeTripBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: TripData) {
            binding.trip = data
        }
    }
}
