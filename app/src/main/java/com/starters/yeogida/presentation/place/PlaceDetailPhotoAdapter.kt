package com.starters.yeogida.presentation.place

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.databinding.ItemPlaceDetailPhotoBinding

class PlaceDetailPhotoAdapter(
    private val context: Context,
    private val imageUrlList: List<String>
) : RecyclerView.Adapter<PlaceDetailPhotoAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding =
            ItemPlaceDetailPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(imageUrlList[position], position)
    }

    override fun getItemCount(): Int {
        return imageUrlList.size
    }

    inner class PlaceViewHolder(
        private val binding: ItemPlaceDetailPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imgUrl: String, position: Int) {
            binding.imgUrl = imgUrl
            binding.position = (position + 1).toString()
            binding.size = imageUrlList.size.toString()
            binding.executePendingBindings()
        }
    }
}