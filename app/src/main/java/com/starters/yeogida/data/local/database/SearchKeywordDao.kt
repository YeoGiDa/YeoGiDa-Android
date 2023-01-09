package com.starters.yeogida.data.local.database

import androidx.room.*
import com.starters.yeogida.data.local.SearchKeyword

@Dao
interface SearchKeywordDao {
    @Query("SELECT * FROM search_word ORDER BY date DESC LIMIT:perPage OFFSET :index * :perPage")
    suspend fun getAllRecentSearch(index: Int = 0, perPage: Int = 10): List<SearchKeyword>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(searchKeyword: SearchKeyword)

    @Query("DELETE FROM search_word where keyword = :text")
    suspend fun deleteKeyword(text: String)

    @Query("DELETE FROM search_word")
    suspend fun deleteAllKeyword()
}