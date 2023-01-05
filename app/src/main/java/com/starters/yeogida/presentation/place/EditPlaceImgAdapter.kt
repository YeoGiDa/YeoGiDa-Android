package com.starters.yeogida.presentation.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.databinding.ItemEditPlacePhotoBinding
import java.io.File

class EditPlaceImgAdapter(
    private val imageFileList: List<File?>,
    private val clickListener: PlaceEditImageClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemEditPlacePhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImgViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        imageFileList[position]?.let {
            if (holder is ImgViewHolder) holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return imageFileList.size
    }

    inner class ImgViewHolder(private val binding: ItemEditPlacePhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageFile: File) {
            binding.imageFile = imageFile
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

}