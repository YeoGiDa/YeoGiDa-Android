package com.starters.yeogida

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class YeogidaFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMessagingService"
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
    }

    // 디바이스가 FCM을 통해서 메시지를 받으면 수행된다.
    //  @remoteMessage: FCM에서 보낸 데이터 정보들을 저장한다.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // FCM을 통해서 전달 받은 정보에 Notification 정보가 있는 경우 알림을 생성한다.
        if (remoteMessage.data["title"] != null) {
            Log.d(TAG, remoteMessage.toString())
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            val type = remoteMessage.data["alarmType"]
            val targetId = remoteMessage.data["targetId"]

            // 알림 설정 여부에 따라 알림 보여주기
            when (type) {
                "NEW_HEART" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (dataStore.notificationLikeIsAllow.first()) {
                            sendNotification(title, body, type, targetId)
                        }
                    }
                }
                "NEW_FOLLOW" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (dataStore.notificationFollowIsAllow.first()) {
                            sendNotification(title, body, type, targetId)
                        }
                    }
                }
                "NEW_COMMENT" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (dataStore.notificationCommentIsAllow.first()) {
                            sendNotification(title, body, type, targetId)
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "수신 에러: data가 비어있습니다.")
        }
    }

    private fun sendNotification(title: String?, body: String?, type: String?, targetId: String?) {
        // id를 고유값으로 지정하여 알림이 개별 표시
        val id = (System.currentTimeMillis() / 7).toInt()
        val requestID = System.currentTimeMillis().toInt()

        val intent = Intent(this, MainActivity::class.java)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            intent.putExtra("type", type)
            intent.putExtra("targetId", targetId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            PendingIntent.getActivity(this, requestID, intent, FLAG_IMMUTABLE)
        } else {
            intent.putExtra("type", type)
            intent.putExtra("targetId", targetId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val channelId = "YeoGidia"
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            channel.apply {
                setShowBadge(false)
            }
        }
        notificationManager.notify(id, notificationBuilder.build())
    }
}
