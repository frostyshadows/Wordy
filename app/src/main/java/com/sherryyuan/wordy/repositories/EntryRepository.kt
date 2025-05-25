package com.sherryyuan.wordy.repositories

import com.sherryyuan.wordy.database.DailyEntryDao
import com.sherryyuan.wordy.entitymodels.DailyEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class EntryRepository @Inject constructor(private val dailyEntryDao: DailyEntryDao) {

    // Add an entry for the given day if one doesn't already exist.
    // Otherwise add or replace wordCount to the existing entry.
    suspend fun insertEntry(
        date: LocalDate,
        wordCount: Int,
        projectId: Long,
        updateWordCountStrategy: UpdateWordCountStrategy,
    ) {
        val entryForDay = dailyEntryDao.getEntryForDate(date)
        if (entryForDay == null) {
            dailyEntryDao.insertEntry(
                DailyEntry(
                    date = date,
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
                date = date,
                wordCount = updatedWordCount,
                projectId = projectId,
            )
        }
    }

    fun getEntries(): Flow<List<DailyEntry>> {
        return dailyEntryDao.getAll()
    }

    enum class UpdateWordCountStrategy {
        ADD,
        REPLACE,
    }
}
