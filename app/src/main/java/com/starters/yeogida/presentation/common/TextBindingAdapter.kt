package com.starters.yeogida.presentation.common

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("double")
fun setDouble(view: TextView, value: Double) {
    view.text = value.toString()
}
