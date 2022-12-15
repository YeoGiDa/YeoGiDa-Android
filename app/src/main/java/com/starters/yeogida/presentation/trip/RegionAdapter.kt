package com.starters.yeogida.presentation.trip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.local.RegionData
import com.starters.yeogida.databinding.ItemRegionBinding

class RegionAdapter(val itemClick: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val regionList = mutableListOf<RegionData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RegionViewHolder) {
            holder.onBind(regionList[position])
        }
        holder.itemView.setOnClickListener {
            itemClick(regionList[position].name)
        }
    }

    override fun getItemCount(): Int {
        return regionList.size
    }

    class RegionViewHolder(val binding: ItemRegionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RegionData) {
            binding.region = data
        }
    }
}
