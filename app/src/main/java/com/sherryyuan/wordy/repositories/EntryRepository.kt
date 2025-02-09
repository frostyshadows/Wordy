package com.sherryyuan.wordy.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.sherryyuan.wordy.database.EntryDao
import com.sherryyuan.wordy.entitymodels.Entry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class EntryRepository @Inject constructor(private val entryDao: EntryDao) {

    // TODO: simplify model to one entry per day
    suspend fun insertEntry(entry: Entry) {
        entryDao.insertEntry(entry)
    }

    fun getEntries(): Flow<List<Entry>> {
        return entryDao.getAll()
    }

    fun getEntriesForToday(): Flow<List<Entry>> {
        return getEntries().map { entries ->
            entries.filter { entry ->
                val midnight = LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                entry.timestamp >= midnight

            }
        }
    }
}
