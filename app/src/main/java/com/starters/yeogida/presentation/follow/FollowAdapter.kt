package com.starters.yeogida.presentation.follow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.R
import com.starters.yeogida.data.remote.response.follow.FollowUserData
import com.starters.yeogida.databinding.ItemFollowBinding

class FollowAdapter(
    private val followUserList: List<FollowUserData>,
    private val viewModel: FollowViewModel
) : ListAdapter<FollowUserData, FollowAdapter.FollowItemViewHolder>(FollowerDiffUtil()) {

    interface ItemClick {
        fun onDeleteBtnClick(view: View, user: FollowUserData)
    }

    var itemClick: ItemClick? = null

    inner class FollowItemViewHolder(private val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(followUserResponse: FollowUserData) {
            binding.user = followUserResponse
            binding.viewModel = viewModel

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowItemViewHolder {
        val binding = ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return FollowItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowItemViewHolder, position: Int) {
        holder.bind(followUserList[position])

        if (itemClick != null) {
            holder.itemView.findViewById<Button>(R.id.btn_follow_delete).setOnClickListener {
                itemClick?.onDeleteBtnClick(it, followUserList[position])
            }
        }
    }
}

class FollowerDiffUtil : DiffUtil.ItemCallback<FollowUserData>() {

    override fun areItemsTheSame(oldItem: FollowUserData, newItem: FollowUserData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FollowUserData, newItem: FollowUserData): Boolean {
        return oldItem.memberId == newItem.memberId
    }
}
