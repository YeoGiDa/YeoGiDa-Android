package com.starters.yeogida.presentation.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.CommentData
import com.starters.yeogida.databinding.ItemCommentBinding
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentAdapter(
    private val commentList: List<CommentData>,
    private val memberId: Long,
    private val viewModel: PlaceViewModel,
    val itemClick: (String, Long) -> Unit
) :
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
            itemClick("삭제", commentList[position].commentId)
        }
        holder.itemView.tv_item_comment_report.setOnClickListener {
            itemClick("신고", commentList[position].commentId)
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentData) {
            binding.comment = comment
            binding.memberId = memberId
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}
