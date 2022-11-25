package com.starters.yeogida.presentation.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.DeleteDialog
import com.starters.yeogida.databinding.ItemFollowBinding

class FollowAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<User>()
    private lateinit var dlg: DeleteDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        dlg = DeleteDialog(parent.context)

        return FollowItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FollowItemViewHolder) {
            holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitUserList(userList: List<User>) {
        items.clear()
        items.addAll(userList)
        notifyDataSetChanged()
    }

    private fun openDialog() {
        dlg.start()
    }

    inner class FollowItemViewHolder(private val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.imageUrl = user.userProfileImageUrl

            binding.btnFollowDelete.setOnClickListener {
                openDialog()
            }

            binding.executePendingBindings()
        }
    }
}