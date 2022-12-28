package com.starters.yeogida.presentation.user.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.common.TripResponse
import com.starters.yeogida.databinding.ItemUserTripBinding

class UserProfileAdapter(
    private val tripList: List<TripResponse>,
    private val viewModel: UserProfileViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemUserTripBinding.inflate(layoutInflater, parent, false)

        return UserTripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserTripViewHolder) holder.bind(tripList[position])
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    inner class UserTripViewHolder(private val binding: ItemUserTripBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: TripResponse) {
            binding.trip = trip
            binding.viewModel = viewModel

            binding.executePendingBindings()
        }
    }
}