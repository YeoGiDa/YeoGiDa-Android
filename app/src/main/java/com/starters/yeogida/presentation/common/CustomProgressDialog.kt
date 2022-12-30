package com.starters.yeogida.presentation.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.starters.yeogida.GlideApp
import com.starters.yeogida.R

class CustomProgressDialog(context: Context) : Dialog(context) {

    private val view = LayoutInflater.from(context).inflate(R.layout.progress_custom, null)
    private val builder = AlertDialog.Builder(context)
        .setView(view)
    private val dialog = builder.create()

    init {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)

        val progressImageView = dialog.findViewById<ImageView>(R.id.iv_progress)

        GlideApp.with(context)
            .asGif()
            .load(R.raw.loading)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
            .override(1200, 1200)
            .into(progressImageView!!)
    }

    fun showDialog() {
        dialog.show()
    }

    fun dismissDialog() {
        this.cancel()
        dialog.dismiss()
    }
}