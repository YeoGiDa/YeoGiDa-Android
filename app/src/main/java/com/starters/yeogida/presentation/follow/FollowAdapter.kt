package com.starters.yeogida.presentation.follow

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.local.FollowUserData
import com.starters.yeogida.databinding.ItemFollowBinding
import com.starters.yeogida.presentation.common.CustomDialog

class FollowAdapter(private val followUserDataList: List<FollowUserData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var dlg: CustomDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        dlg = CustomDialog(parent.context)

        return FollowItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FollowItemViewHolder) {
            holder.bind(followUserDataList[position])
        }
    }

    override fun getItemCount(): Int {
        return followUserDataList.size
    }


    fun openDialog(context: Context) {
        val dialog = CustomDialog(context)
        dialog.setTitle("정말 삭제하시겠습니까?")
        dialog.setNegativeBtn("취소") {
            dialog.dismissDialog()
        }
        dialog.setPositiveBtn("삭제") {
            dialog.dismissDialog()
        }
        dialog.showDialog()
    }

    inner class FollowItemViewHolder(private val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(followUserData: FollowUserData) {
            binding.user = followUserData

            binding.btnFollowDelete.setOnClickListener {
                openDialog(itemView.context)
            }

            binding.executePendingBindings()
        }
    }
}
