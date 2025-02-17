package com.sherryyuan.wordy.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sherryyuan.wordy.entitymodels.DailyEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyEntryDao {

    @Query(value = "SELECT * FROM DailyEntry")
    fun getAll(): Flow<List<DailyEntry>>

    @Query(value = "SELECT * FROM DailyEntry WHERE timestamp= :timestamp")
    suspend fun getEntryForTimestamp(timestamp: Long): DailyEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DailyEntry)

    @Query(value = "UPDATE DailyEntry SET timestamp= :timestamp, wordCount= :wordCount, projectId = :projectId WHERE id = :entryId")
    suspend fun updateEntry(entryId: Long, timestamp: Long, wordCount: Int, projectId: Long)

    @Delete
    suspend fun deleteEntry(entry: DailyEntry)
}
