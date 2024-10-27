package com.sherryyuan.wordy.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sherryyuan.wordy.entitymodels.Entry
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Query(value = "SELECT * FROM Entry")
    fun getAll(): Flow<List<Entry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: Entry)

    @Query(value = "UPDATE Entry SET timestamp= :timestamp, wordCount= :wordCount, projectId = :projectId WHERE id = :entryId")
    suspend fun updateEntry(entryId: Int, timestamp: Long, wordCount: Int, projectId: Int)

    @Delete
    suspend fun deleteEntry(entry: Entry)
}
