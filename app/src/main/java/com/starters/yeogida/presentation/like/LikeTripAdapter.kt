package com.starters.yeogida.presentation.like

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.local.LikeTripData
import com.starters.yeogida.databinding.ItemTripBinding

class LikeTripAdapter(private val likeTripList: List<LikeTripData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikeTripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LikeTripViewHolder) {
            holder.bind(likeTripList[position])
        }
    }

    override fun getItemCount(): Int {
        return likeTripList.size
    }


    inner class LikeTripViewHolder(private val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(likeTrip: LikeTripData) {
            binding.likeTrip = likeTrip
            binding.executePendingBindings()
        }
    }
}