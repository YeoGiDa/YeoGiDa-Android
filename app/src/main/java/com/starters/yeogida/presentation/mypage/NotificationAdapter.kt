package com.starters.yeogida.presentation.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.starters.yeogida.data.remote.response.mypage.NotificationData
import com.starters.yeogida.databinding.ItemAlarmBinding
import java.util.*

class NotificationAdapter(val itemClick: (String, Long) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val notificationList = mutableListOf<NotificationData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NotificationViewHolder) {
            holder.onBind(notificationList[position])
        }
        holder.itemView.setOnClickListener {
            itemClick(notificationList[position].alarmType, notificationList[position].targetId)
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class NotificationViewHolder(val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: NotificationData) {
            binding.notification = data
        }
    }
}
