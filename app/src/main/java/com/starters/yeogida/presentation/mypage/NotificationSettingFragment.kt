package com.starters.yeogida.presentation.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.starters.yeogida.databinding.FragmentNotificationSettingBinding

class NotificationSettingFragment : Fragment() {
    private lateinit var binding: FragmentNotificationSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationSettingBinding.inflate(inflater, container, false)
        binding.view = this
        return binding.root
    }

    // 각 알림 switch check 변경 시
    fun changeSwitch(view: View) {
        with(binding) {
            // 각 알림 switch check를 모두 true 변경 시
            if (switchNotificationComment.isChecked && switchNotificationLike.isChecked && switchNotificationFollow.isChecked) {
                switchNotificationAll.isChecked = true
            }

            // 각 알림 switch check를 모두 false로 변경 시
            if (!switchNotificationComment.isChecked || !switchNotificationLike.isChecked || !switchNotificationFollow.isChecked) {
                switchNotificationAll.isChecked = false
            }
        }
    }

    // 전체 알림 switch 변경 시
    fun changeSwitchAll(view: View) {
        if (binding.switchNotificationAll.isChecked) {
            isCheckedNotification(b1 = true, b2 = true, b3 = true)
        } else {
            isCheckedNotification(b1 = false, b2 = false, b3 = false)
        }
    }

    private fun isCheckedNotification(b1: Boolean, b2: Boolean, b3: Boolean) {
        with(binding) {
            switchNotificationComment.isChecked = b1
            switchNotificationLike.isChecked = b2
            switchNotificationFollow.isChecked = b3
        }
    }
}
