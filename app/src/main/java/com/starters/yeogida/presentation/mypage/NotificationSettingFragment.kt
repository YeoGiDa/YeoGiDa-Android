package com.starters.yeogida.presentation.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.BuildConfig
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

            saveNotification(
                like = switchNotificationLike.isChecked,
                follow = switchNotificationFollow.isChecked,
                comment = switchNotificationComment.isChecked
            )
        }
    }

    // 전체 알림 switch 변경 시
    fun changeSwitchAll(view: View) {
        if (binding.switchNotificationAll.isChecked) {
            isCheckedNotification(like = true, follow = true, comment = true)
            saveNotification(like = true, follow = true, comment = true)
        } else {
            isCheckedNotification(like = false, follow = false, comment = false)
            saveNotification(like = false, follow = false, comment = false)
        }
    }

    private fun isCheckedNotification(like: Boolean, follow: Boolean, comment: Boolean) {
        with(binding) {
            switchNotificationLike.isChecked = like
            switchNotificationFollow.isChecked = follow
            switchNotificationComment.isChecked = comment
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

    // 설정 화면으로 이동
    fun moveToSetting(view: View) {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
        startActivity(intent)
    }
}
