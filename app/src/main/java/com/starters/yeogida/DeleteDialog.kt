package com.starters.yeogida

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.widget.AppCompatButton

class DeleteDialog(private val context: Context) {

    private lateinit var btnDelete: AppCompatButton
    private lateinit var btnCancel: AppCompatButton
    private lateinit var dlg: Dialog

    fun start() {
        dlg = Dialog(context).apply {
            setContentView(R.layout.dialog_delete)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        btnDelete = dlg.findViewById(R.id.btn_dialog_delete)
        btnDelete.setOnClickListener {
            onDeleteClicked()
            dlg.dismiss()
        }

        btnCancel = dlg.findViewById(R.id.btn_dialog_cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }

    private fun onDeleteClicked() {

    }
}


