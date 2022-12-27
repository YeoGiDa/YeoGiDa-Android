package com.starters.yeogida.presentation.around

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.trip.TripLikeUserData
import com.starters.yeogida.databinding.ItemTripLikeUserBinding

class TripLikeUserListAdapter(
    private val userList: List<TripLikeUserData>,
    private val viewModel: AroundPlaceViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTripLikeUserBinding.inflate(layoutInflater, parent, false)

        return ItemTripLikeUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemTripLikeUserViewHolder) {
            holder.bind(userList[position])
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ItemTripLikeUserViewHolder(private val binding: ItemTripLikeUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: TripLikeUserData) {
            binding.user = user
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}