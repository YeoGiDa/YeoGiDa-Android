package com.starters.yeogida.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.trip.SearchTrip
import com.starters.yeogida.databinding.ItemSearchTripBinding

class SearchTripAdapter(
    private val tripList: List<SearchTrip>,
    private val viewModel: SearchViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchTripBinding.inflate(layoutInflater, parent, false)
        return ItemSearchTripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        tripList[position]?.let {
            if (holder is ItemSearchTripViewHolder) holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    inner class ItemSearchTripViewHolder(private val binding: ItemSearchTripBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: SearchTrip) {
            binding.trip = trip
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

}