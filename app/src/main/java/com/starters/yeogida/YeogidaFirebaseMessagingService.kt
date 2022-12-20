package com.starters.yeogida

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class YeogidaFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMessagingService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
    }

    // 디바이스가 FCM을 통해서 메시지를 받으면 수행된다.
    //  @remoteMessage: FCM에서 보낸 데이터 정보들을 저장한다.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // FCM을 통해서 전달 받은 정보에 Notification 정보가 있는 경우 알림을 생성한다.
        if (remoteMessage.notification != null) {
            sendNotification(remoteMessage)
        } else {
            Log.d(TAG, "수신 에러: Notification이 비어있습니다.")
        }
    }

    // FCM에서 보낸 정보를 바탕으로 디바이스에 Notification을 생성한다.
    private fun sendNotification(remoteMessage: RemoteMessage) {
        // id를 고유값으로 지정하여 알림이 개별 표시
        val id = (System.currentTimeMillis() / 7).toInt()
        var title = remoteMessage.notification!!.title
        var body = remoteMessage.notification!!.body

        var intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = "Channel ID"
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_HIGH)

        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(id, notificationBuilder.build())
    }
}