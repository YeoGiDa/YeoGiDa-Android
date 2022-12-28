package com.starters.yeogida

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.starters.yeogida.databinding.ActivityMainBinding
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.presentation.user.profile.UserProfileActivity
import gun0912.tedimagepicker.util.ToastUtil.context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        initBottomNavigation()
        initNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this)
        }

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        checkNotification()
    }

    private fun initBottomNavigation() {
        val navController =
            supportFragmentManager.findFragmentById(R.id.container_main)?.findNavController()

        navController?.let { // null이 아닐 때만 확인하기 위해 let 사용
            binding.navigationMain.setupWithNavController(it)
        }
    }

    // 알림 포그라운드일 때
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        initNotification()
    }

    private fun initNotification() {
        when (intent.getStringExtra("type").toString()) {
            "NEW_FOLLOW" -> {
                val navController = supportFragmentManager.findFragmentById(R.id.container_main)
                    ?.findNavController()

                navController?.let { // null이 아닐 때만 확인하기 위해 let 사용
                    binding.navigationMain.setupWithNavController(it)
                }

                val memberId = intent.getStringExtra("targetId")?.toLong()

                Intent(this, UserProfileActivity::class.java).apply {
                    putExtra("memberId", memberId)
                    startActivity(this)
                }
            }
            "NEW_COMMENT" -> {
                val tripId = intent.getStringExtra("targetId")?.split(",")?.get(0)?.toLong()
                val placeId = intent.getStringExtra("targetId")?.split(",")?.get(1)?.toLong()
                val intent = Intent(this, PlaceActivity::class.java)
                intent.putExtra("type", "comment")
                intent.putExtra("tripId", tripId)
                intent.putExtra("placeId", placeId)
                startActivity(intent)
            }
            "NEW_HEART" -> {
                val tripId = intent.getStringExtra("targetId")?.toLong()
                val intent = Intent(this, PlaceActivity::class.java)
                intent.putExtra("type", "heart")
                intent.putExtra("tripId", tripId)
                startActivity(intent)
            }
            else -> {}
        }
    }

    private fun createNotificationChannel(context: Context) {
        val name = "YeoGidia"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channelId = "${context.packageName}-$name"
        val channel = NotificationChannel(channelId, name, importance)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkNotification() {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            saveNotificationAllow(like = true, follow = true, comment = true)
        } else {
            saveNotificationAllow(like = false, follow = false, comment = false)
        }
    }

    private fun saveNotificationAllow(like: Boolean, follow: Boolean, comment: Boolean) {
        val dataStore = YeogidaApplication.getInstance().getDataStore()
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveNotificationLikeIsAllow(like)
            dataStore.saveNotificationFollowIsAllow(follow)
            dataStore.saveNotificationCommentIsAllow(comment)
        }
    }
}
