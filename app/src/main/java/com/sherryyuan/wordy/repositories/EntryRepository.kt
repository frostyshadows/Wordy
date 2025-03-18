package com.sherryyuan.wordy.repositories

import com.sherryyuan.wordy.database.DailyEntryDao
import com.sherryyuan.wordy.entitymodels.DailyEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class EntryRepository @Inject constructor(private val dailyEntryDao: DailyEntryDao) {

    // Add an entry for the given day if one doesn't already exist.
    // Otherwise add or replace wordCount to the existing entry.
    suspend fun insertEntry(
        timestamp: Long,
        wordCount: Int,
        projectId: Long,
        updateWordCountStrategy: UpdateWordCountStrategy,
    ) {
        val startOfDayTimestamp = getStartOfDayTimestamp(timestamp)
        val entryForDay = dailyEntryDao.getEntryForTimestamp(startOfDayTimestamp)
        if (entryForDay == null) {
            dailyEntryDao.insertEntry(
                DailyEntry(
                    timestamp = startOfDayTimestamp,
                    wordCount = wordCount,
                    projectId = projectId,
                )
            )
        } else {
            val updatedWordCount = when (updateWordCountStrategy) {
                UpdateWordCountStrategy.ADD -> entryForDay.wordCount + wordCount
                UpdateWordCountStrategy.REPLACE -> wordCount
            }
            dailyEntryDao.updateEntry(
                entryId = entryForDay.id,
                timestamp = startOfDayTimestamp,
                wordCount = updatedWordCount,
                projectId = projectId,
            )
        }
    }

    fun getEntries(): Flow<List<DailyEntry>> {
        return dailyEntryDao.getAll()
    }

    private fun getStartOfDayTimestamp(timeStamp: Long): Long =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault())
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    enum class UpdateWordCountStrategy {
        ADD,
        REPLACE,
    }
}
