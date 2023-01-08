package com.starters.yeogida.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.trip.RankTrip
import com.starters.yeogida.databinding.ItemPopularKeywordBinding

class PopularSearchAdapter(
    private val rankList: List<RankTrip>,

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPopularKeywordBinding.inflate(inflater, parent, false)
        return ItemPopularTripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        rankList[position]?.let {
            if (holder is ItemPopularTripViewHolder) holder.bind(it, position)
        }
    }

    override fun getItemCount(): Int {
        return rankList.size
    }

    inner class ItemPopularTripViewHolder(private val binding: ItemPopularKeywordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rankTrip: RankTrip, position: Int) {
            binding.rank = position + 1
            binding.rankTrip = rankTrip
            binding.executePendingBindings()
        }
    }
}