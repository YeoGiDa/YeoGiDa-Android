package com.starters.yeogida.util

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class LocalDataTypeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromZonedDateTime(value: ZonedDateTime): Long {
            return value.toEpochSecond()
        }

        @TypeConverter
        @JvmStatic
        fun toZonedDateTime(value: Long): ZonedDateTime {
            val instant = Instant.ofEpochSecond(value)
            return ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"))
        }
    }
}