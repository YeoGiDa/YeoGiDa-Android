package com.starters.yeogida.presentation.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentAlarmBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.util.customEnqueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding

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
    }

    private fun initAdapter() {
        val notificationAdapter = NotificationAdapter()
        binding.rvNotification.adapter = notificationAdapter

        CoroutineScope(Dispatchers.IO).launch {
            YeogidaClient.myPageService.getNotificationList(
                YeogidaApplication.getInstance().getDataStore().userBearerToken.first()
            ).customEnqueue(
                onSuccess = {
                    if (it.code == 200) {
                        it.data?.let { data -> notificationAdapter.notificationList.addAll(data.alarmList) }
                        notificationAdapter.notifyDataSetChanged()
                    }
                }
            )
        }
    }
}
