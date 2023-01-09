package com.starters.yeogida.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.starters.yeogida.data.local.SearchKeyword
import com.starters.yeogida.util.LocalDataTypeConverter

@Database(entities = [SearchKeyword::class], version = 1)
@TypeConverters(LocalDataTypeConverter::class)
abstract class YeogidaDatabase : RoomDatabase() {
    abstract fun searchKeywordDao(): SearchKeywordDao

    companion object {
        private var dbName = "localDB"
        private var INSTANCE: YeogidaDatabase? = null

        fun getInstance(context: Context): YeogidaDatabase? {
            if (INSTANCE == null) {
                synchronized(YeogidaDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        YeogidaDatabase::class.java,
                        dbName
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}