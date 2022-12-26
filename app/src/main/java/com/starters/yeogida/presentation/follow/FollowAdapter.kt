package com.starters.yeogida.presentation.follow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.R
import com.starters.yeogida.data.remote.response.follow.FollowUserData
import com.starters.yeogida.databinding.ItemFollowBinding

class FollowAdapter(
    private val followUserList: List<FollowUserData>,
    private val viewModel: FollowViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClick {
        fun onDeleteBtnClick(view: View, user: FollowUserData)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return FollowItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FollowItemViewHolder) {
            holder.bind(followUserList[position])

            if (itemClick != null) {
                holder.itemView.findViewById<Button>(R.id.btn_follow_delete).setOnClickListener {
                    itemClick?.onDeleteBtnClick(it, followUserList[position])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return followUserList.size
    }

    inner class FollowItemViewHolder(private val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(followUserResponse: FollowUserData) {
            binding.user = followUserResponse
            binding.viewModel = viewModel

            binding.executePendingBindings()
        }
    }
}
