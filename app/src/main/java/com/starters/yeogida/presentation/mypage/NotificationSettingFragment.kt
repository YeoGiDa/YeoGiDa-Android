package com.starters.yeogida.presentation.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentNotificationSettingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationSettingFragment : Fragment() {
    private lateinit var binding: FragmentNotificationSettingBinding
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationSettingBinding.inflate(inflater, container, false)
        binding.view = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwitchChecked()
        initClickListener()
    }

    private fun initSwitchChecked() {
        CoroutineScope(Dispatchers.IO).launch {
            with(binding) {
                switchNotificationFollow.isChecked = dataStore.notificationFollowIsAllow.first()
                switchNotificationLike.isChecked = dataStore.notificationLikeIsAllow.first()
                switchNotificationComment.isChecked = dataStore.notificationCommentIsAllow.first()

                if (switchNotificationComment.isChecked && switchNotificationLike.isChecked && switchNotificationFollow.isChecked) {
                    switchNotificationAll.isChecked = true
                }
            }
        }
    }

// 각 알림 switch check 변경 시
    fun changeSwitch(view: View) {
        with(binding) {
            // 각 알림 switch check를 모두 true 변경 시
            if (switchNotificationComment.isChecked && switchNotificationLike.isChecked && switchNotificationFollow.isChecked) {
                switchNotificationAll.isChecked = true
            }

            // 각 알림 switch check 중 false로 변경 시
            if (!switchNotificationComment.isChecked || !switchNotificationLike.isChecked || !switchNotificationFollow.isChecked) {
                switchNotificationAll.isChecked = false
            }

            saveNotification(like = switchNotificationLike.isChecked, follow = switchNotificationFollow.isChecked, comment = switchNotificationComment.isChecked)
        }
    }

    // 전체 알림 switch 변경 시
    fun changeSwitchAll(view: View) {
        if (binding.switchNotificationAll.isChecked) {
            isCheckedNotification(b1 = true, b2 = true, b3 = true)
            saveNotification(like = true, follow = true, comment = true)
        } else {
            isCheckedNotification(b1 = false, b2 = false, b3 = false)
            saveNotification(like = false, follow = false, comment = false)
        }
    }

    private fun isCheckedNotification(b1: Boolean, b2: Boolean, b3: Boolean) {
        with(binding) {
            switchNotificationComment.isChecked = b1
            switchNotificationLike.isChecked = b2
            switchNotificationFollow.isChecked = b3
        }
    }

    private fun saveNotification(like: Boolean, follow: Boolean, comment: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveNotificationLikeIsAllow(like)
            dataStore.saveNotificationFollowIsAllow(follow)
            dataStore.saveNotificationCommentIsAllow(comment)
        }
    }

    private fun initClickListener() {
        binding.tbNotificationSetting.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}
