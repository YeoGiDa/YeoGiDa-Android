package com.starters.yeogida.presentation.like

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.R
import com.starters.yeogida.data.local.LikeTripData
import com.starters.yeogida.databinding.ItemLikeTripBinding

class LikeTripAdapter(
    private val likeTripList: List<LikeTripData>,
    private val viewModel: LikeTripViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClick {
        fun onClick(view: View, likeTrip: LikeTripData)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemLikeTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikeTripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LikeTripViewHolder) {
            holder.bind(likeTripList[position])

            if (itemClick != null) {
                holder.itemView.findViewById<ImageButton>(R.id.btn_like).setOnClickListener {
                    itemClick?.onClick(it, likeTripList[position])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return likeTripList.size
    }

    inner class LikeTripViewHolder(private val binding: ItemLikeTripBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(likeTrip: LikeTripData) {
            binding.likeTrip = likeTrip
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}
