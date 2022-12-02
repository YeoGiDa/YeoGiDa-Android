package com.starters.yeogida.presentation.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.starters.yeogida.R
import kotlinx.android.synthetic.main.dialog_custom.view.*

class CustomDialog(context: Context) : Dialog(context) {
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
    val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        .setView(view)
    val dialog = builder.create()

    init {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun setPositiveBtn(s: String, listener: (v: View) -> Unit) {
        view.btn_dialog_confirm.text = s
        view.btn_dialog_confirm.setOnClickListener(
            listener
        )
    }

    fun setNegativeBtn(s: String, listener: (v: View) -> Unit) {
        view.btn_dialog_cancel.text = s
        view.btn_dialog_cancel.setOnClickListener(
            listener
        )
    }

    fun setTitle(s: String) {
        view.tv_dialog_text.text = s
    }

    fun showDialog() {
        dialog.show()
    }

    fun dismissDialog() {
        this.cancel()
        dialog.dismiss()
        dismiss()
        setOnDismissListener {
            Toast.makeText(context, "dismiss", Toast.LENGTH_SHORT).show()
        }
    }
}
