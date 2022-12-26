package com.starters.yeogida.presentation.place

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
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
        if (intent.getStringExtra("type") == "comment") {
            val navController =
                supportFragmentManager.findFragmentById(R.id.container_place)?.findNavController()
            navController?.navigate(
                R.id.action_aroundPlace_to_placeDetail,
                bundleOf("placeId" to intent.getLongExtra("placeId", 0))
            )
        }
    }

    private fun getTripId() {
        val tripId = intent.getLongExtra("tripId", 0)
        intent.putExtra("tripId", tripId)
    }
}
