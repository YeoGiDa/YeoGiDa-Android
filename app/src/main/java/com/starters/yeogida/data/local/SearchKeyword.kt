package com.starters.yeogida.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "search_word")
data class SearchKeyword(
    @PrimaryKey @ColumnInfo(name = "keyword") val keyword: String,
    @ColumnInfo(name = "date") var date: ZonedDateTime = ZonedDateTime.now()
)
