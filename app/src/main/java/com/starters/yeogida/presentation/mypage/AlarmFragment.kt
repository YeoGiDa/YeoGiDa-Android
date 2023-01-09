package com.starters.yeogida.presentation.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.R
import com.starters.yeogida.databinding.FragmentAlarmBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.presentation.user.profile.UserProfileActivity
import com.starters.yeogida.util.customEnqueue

class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initNavigation()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initAdapter() {
        val notificationAdapter =
            NotificationAdapter { type: String, followerId: Long, placeId: Long, tripId: Long ->
                moveToPage(type, followerId, placeId, tripId)
            }
        binding.rvNotification.adapter = notificationAdapter

        YeogidaClient.myPageService.getNotificationList().customEnqueue(
            onSuccess = {
                if (it.code == 200) {
                    if (it.data?.alarmList?.isEmpty() == true) {
                        binding.layoutAlarmEmpty.visibility = View.VISIBLE
                    } else {
                        binding.layoutAlarmEmpty.visibility = View.GONE
                        it.data?.let { data -> notificationAdapter.notificationList.addAll(data.alarmList) }
                        notificationAdapter.notifyDataSetChanged()
                    }
                }
            }
        )
    }

    private fun initNavigation() {
        binding.tbAlarm.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun moveToPage(type: String, followerId: Long, placeId: Long, tripId: Long) {
        when (type) {
            "NEW_FOLLOW" -> {
                val intent = Intent(mContext, UserProfileActivity::class.java)
                intent.putExtra("memberId", followerId)
                startActivity(intent)
            }
            "NEW_COMMENT" -> {
                val intent = Intent(mContext, PlaceActivity::class.java)
                intent.putExtra("type", "comment_alarm")
                intent.putExtra("placeId", placeId)
                startActivity(intent)
            }
            "NEW_HEART" -> {
                val intent = Intent(mContext, PlaceActivity::class.java)
                intent.putExtra("type", "heart")
                intent.putExtra("tripId", tripId)
                startActivity(intent)
            }
            else -> {}
        }
    }
}
