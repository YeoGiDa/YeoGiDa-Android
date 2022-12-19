package com.starters.yeogida.presentation.place

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.databinding.ItemAddPlacePhotoBinding

class AddPlaceImgAdapter(
    private val uriList: List<Uri>,
    private val clickListener: PlaceImageClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemAddPlacePhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImgViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImgViewHolder) holder.bind(uriList[position])
    }

    override fun getItemCount(): Int {
        return uriList.size
    }

    inner class ImgViewHolder(private val binding: ItemAddPlacePhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            binding.imageUri = uri
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

}