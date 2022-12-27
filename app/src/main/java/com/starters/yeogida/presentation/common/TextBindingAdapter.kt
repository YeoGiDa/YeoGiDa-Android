package com.starters.yeogida.presentation.common

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@BindingAdapter("double")
fun setDouble(view: TextView, value: Double) {
    view.text = value.toString()
}

@BindingAdapter("float")
fun setFloat(view: TextView, value: Float) {
    view.text = value.toString()
}

@BindingAdapter("count")
fun setInt(view: TextView, value: Int) {
    view.text = value.toString()
}

@BindingAdapter("position", "size")
fun setPhotoCounter(view: TextView, position: String, size: String) {
    view.text = "$position / $size"
}

@BindingAdapter("date")
fun translateDate(view: TextView, dateTime: String) {
    val now = LocalDateTime.now()
    val convertTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val compareSecondTime = ChronoUnit.SECONDS.between(convertTime, now)
    val compareMinuteTime = ChronoUnit.MINUTES.between(convertTime, now)
    val compareHourTime = ChronoUnit.HOURS.between(convertTime, now)
    val compareDayTime = ChronoUnit.DAYS.between(convertTime, now)
    val compareMonthTime = ChronoUnit.MONTHS.between(convertTime, now)
    when {
        compareSecondTime < 60 -> view.text = "${compareSecondTime}초전"
        compareMinuteTime < 60 -> view.text = "${compareMinuteTime}분전"
        compareHourTime < 24 -> view.text = "${compareHourTime}시간전"
        compareDayTime < when (now.monthValue) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            2 -> 28
            else -> 30
        } -> view.text = "${compareDayTime}일전"
        else -> view.text = "${compareMonthTime}달전"
    }
}
