package com.starters.yeogida.presentation.like

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.common.TripResponse
import com.starters.yeogida.databinding.ItemLikeTripBinding

class LikeTripAdapter(
    private val likeTripList: List<TripResponse>,
    private val viewModel: LikeTripViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemLikeTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikeTripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        likeTripList[position]?.let {
            if (holder is LikeTripViewHolder) holder.bind(likeTripList[position])
        }

    }

    override fun getItemCount(): Int {
        return likeTripList.size
    }

    inner class LikeTripViewHolder(private val binding: ItemLikeTripBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: TripResponse) {
            binding.trip = trip
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}
