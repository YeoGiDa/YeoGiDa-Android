package com.starters.yeogida.presentation.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.local.SortData
import com.starters.yeogida.databinding.ItemSortBinding

class SortAdapter(val itemClick: (String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val sortList = mutableListOf<SortData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSortBinding.inflate(inflater, parent, false)

        return SortViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SortViewHolder) holder.bind(sortList[position])
        holder.itemView.setOnClickListener {
            itemClick(sortList[position].sortName)
        }
    }

    override fun getItemCount(): Int {
        return sortList.size
    }

    inner class SortViewHolder(private val binding: ItemSortBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sortData: SortData) {
            binding.sort = sortData
        }
    }
}