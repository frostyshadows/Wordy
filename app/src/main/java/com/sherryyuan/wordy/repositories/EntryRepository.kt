package com.sherryyuan.wordy.repositories

import com.sherryyuan.wordy.database.EntryDao
import com.sherryyuan.wordy.entitymodels.Entry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EntryRepository @Inject constructor(private val entryDao: EntryDao) {

    suspend fun insertEntry(entry: Entry) {
        entryDao.insertEntry(entry)
    }

    fun getEntries(): Flow<List<Entry>> {
        return entryDao.getAll()
    }
}
