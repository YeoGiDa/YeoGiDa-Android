package com.starters.yeogida.presentation.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.mypage.MyTrip
import com.starters.yeogida.databinding.ItemMyTripBinding

class MyTripAdapter(
    private val tripList: List<MyTrip>,
    private val viewModel: MyPageViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMyTripBinding.inflate(layoutInflater, parent, false)

        return MyTripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyTripViewHolder) {
            tripList[position]?.let {
                holder.bind(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    inner class MyTripViewHolder(private val binding: ItemMyTripBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: MyTrip) {
            binding.trip = trip
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}