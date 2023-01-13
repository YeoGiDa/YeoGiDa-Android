package com.starters.yeogida.presentation.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.place.PlaceImg
import com.starters.yeogida.databinding.ItemPlaceDetailPhotoBinding
import com.starters.yeogida.util.ImageUtil

class PlaceDetailPhotoAdapter(
    private val imageUrlList: List<PlaceImg>
) : RecyclerView.Adapter<PlaceDetailPhotoAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding =
            ItemPlaceDetailPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        imageUrlList[position]?.let { url ->
            holder.bind(url)

            if (position <= imageUrlList.size) {
                val endPosition = if (position + 6 > imageUrlList.size) {
                    imageUrlList.size
                } else {
                    position + 6
                }
                imageUrlList.subList(position, endPosition).map { it }.forEach {
                    ImageUtil.preload(holder.itemView.context, url.imgUrl)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imageUrlList.size
    }

    inner class PlaceViewHolder(
        private val binding: ItemPlaceDetailPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(placeImg: PlaceImg) {
            binding.imgUrl = placeImg.imgUrl
            binding.executePendingBindings()
        }
    }
}