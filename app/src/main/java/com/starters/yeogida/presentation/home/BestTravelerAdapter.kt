package com.starters.yeogida.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.local.BestTravelerData
import com.starters.yeogida.databinding.ItemBestTravelerBinding

class BestTravelerAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val bestTravelerList = mutableListOf<BestTravelerData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemBestTravelerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BestTravelerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BestTravelerViewHolder) {
            holder.onBind(bestTravelerList[position])
        }
    }

    override fun getItemCount(): Int {
        return bestTravelerList.size
    }

    inner class BestTravelerViewHolder(val binding: ItemBestTravelerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: BestTravelerData) {
            binding.traveler = data
        }
    }
}
