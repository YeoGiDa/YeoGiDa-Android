package com.starters.yeogida.presentation.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.local.CommentData
import com.starters.yeogida.databinding.ItemCommentBinding

class CommentAdapter(private val commentList: List<CommentData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommentAdapter.CommentViewHolder) {
            holder.bind(commentList[position])
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentData) {
            binding.comment = comment
            binding.executePendingBindings()
        }
    }
}