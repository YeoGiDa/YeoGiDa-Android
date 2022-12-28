package com.starters.yeogida.presentation.place

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.R
import com.starters.yeogida.databinding.ActivityPlaceBinding

class PlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceBinding.inflate(layoutInflater)

        initNavigation()
        getTripId()

        setContentView(binding.root)
    }

    private fun initNavigation() {
        val navController =
            supportFragmentManager.findFragmentById(R.id.container_place)?.findNavController()
        if (intent.getStringExtra("type") == "comment" || intent.getStringExtra("type") == "around") { // 백그라운드 댓글 알림 or 둘러보기에서 이동
            navController?.navigate(
                R.id.action_aroundPlace_to_placeDetail,
                bundleOf("placeId" to intent.getLongExtra("placeId", 0))
            )
        } else if (intent.getStringExtra("type") == "comment_alarm") { // 알림 목록 - 댓글에서 이동
            navController?.navigate(
                R.id.action_aroundPlace_to_placeDetail,
                bundleOf("placeId" to intent.getLongExtra("placeId", 0), "type" to "comment_alarm")
            )
        }
    }

    private fun getTripId() {
        val tripId = intent.getLongExtra("tripId", 0)
        intent.putExtra("tripId", tripId)
    }
}
