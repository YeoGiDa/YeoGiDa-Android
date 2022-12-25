package com.starters.yeogida

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.starters.yeogida.databinding.ActivityMainBinding
import com.starters.yeogida.presentation.place.PlaceActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        initBottomNavigation()
        initNotification()

        setContentView(binding.root)
    }

    private fun initBottomNavigation() {
        val navController =
            supportFragmentManager.findFragmentById(R.id.container_main)?.findNavController()

        navController?.let { // null이 아닐 때만 확인하기 위해 let 사용
            binding.navigationMain.setupWithNavController(it)
        }
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
                navController?.navigate(R.id.navigation_follow)
                navController?.navigate(
                    R.id.action_follow_to_userProfile,
                    bundleOf("memberId" to memberId)
                )
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
}
