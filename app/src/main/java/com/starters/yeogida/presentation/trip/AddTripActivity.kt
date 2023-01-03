package com.starters.yeogida.presentation.trip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.starters.yeogida.databinding.ActivityAddTripBinding

class AddTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTripBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkEdit()
    }

    private fun checkEdit() {
        val type = intent.getStringExtra("type")
        type?.let {
            if (type == "edit") {
                intent.putExtra("type", type)

                val tripId = intent.getLongExtra("tripId", 0)
                intent.putExtra("tripId", tripId)

                val region = intent.getStringExtra("region")
                region?.let {
                    intent.putExtra("region", region)
                }

                val title = intent.getStringExtra("title")
                title?.let {
                    intent.putExtra("title", title)
                }

                val subTitle = intent.getStringExtra("subTitle")
                subTitle?.let {
                    intent.putExtra("subTitle", subTitle)
                }

                val imgUrl = intent.getStringExtra("imgUrl")
                imgUrl?.let {
                    intent.putExtra("imgUrl", imgUrl)
                }
            }
        }
    }
}
