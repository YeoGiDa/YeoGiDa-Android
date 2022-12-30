package com.starters.yeogida.presentation.around

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.R
import com.starters.yeogida.data.remote.response.common.PlaceData
import com.starters.yeogida.databinding.ItemPlaceBinding

class AroundPlaceAdapter(private val viewModel: AroundPlaceViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val aroundPlaceList = mutableListOf<PlaceData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AroundPlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AroundPlaceViewHolder) {
            holder.onBind(aroundPlaceList[position])
            when (aroundPlaceList[position].tag) {
                "식당" -> holder.binding.tvPlaceTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_red_17)
                "카페" -> holder.binding.tvPlaceTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_orange_17)
                "바" -> holder.binding.tvPlaceTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_yellow_17)
                "관광지" -> holder.binding.tvPlaceTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_green_17)
                "쇼핑" -> holder.binding.tvPlaceTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_blue_17)
                "숙소" -> holder.binding.tvPlaceTag.setBackgroundResource(R.drawable.rectangle_fill_pastel_purple_17)
                "기타" -> holder.binding.tvPlaceTag.setBackgroundResource(R.drawable.rectangle_fill_gray200_17)
            }
        }
    }

    override fun getItemCount(): Int {
        return aroundPlaceList.size
    }

    inner class AroundPlaceViewHolder(val binding: ItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: PlaceData) {
            binding.viewModel = viewModel
            binding.place = data

            binding.executePendingBindings()
        }
    }
}
