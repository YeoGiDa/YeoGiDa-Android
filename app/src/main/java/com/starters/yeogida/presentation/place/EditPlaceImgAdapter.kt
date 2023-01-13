package com.starters.yeogida.presentation.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.databinding.ItemEditPlacePhotoBinding
import com.starters.yeogida.util.ImageUtil
import java.io.File

class EditPlaceImgAdapter(
    private val imageFileList: List<File?>,
    private val clickListener: PlaceEditImageClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEditPlacePhotoBinding.inflate(inflater, parent, false)

        return ImgViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImgViewHolder) {
            imageFileList[position]?.let { file ->
                holder.bind(file)

                if (position <= imageFileList.size) {
                    val endPosition = if (position + 6 > imageFileList.size) {
                        imageFileList.size
                    } else {
                        position + 6
                    }
                    imageFileList.subList(position, endPosition).map { it }.forEach {
                        ImageUtil.preload(holder.itemView.context, file)
                    }
                }
            }
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