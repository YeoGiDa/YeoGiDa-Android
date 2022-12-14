package com.starters.yeogida.presentation.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.local.CommentData
import com.starters.yeogida.databinding.ItemCommentBinding
import com.starters.yeogida.presentation.common.OnItemClick
import kotlinx.android.synthetic.main.fragment_place_detail.view.*
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentAdapter(private val commentList: List<CommentData>, private val onItemClick: OnItemClick) :
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
        holder.itemView.tv_item_comment_delete.setOnClickListener {
            onItemClick.onClick("삭제")
        }
        holder.itemView.tv_item_comment_report.setOnClickListener {
            onItemClick.onClick("신고")
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