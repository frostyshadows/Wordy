package com.sherryyuan.wordy

import com.sherryyuan.wordy.database.EntryDao
import com.sherryyuan.wordy.models.Entry
import javax.inject.Inject

class EntryRepository @Inject constructor(private val entryDao: EntryDao) {

    suspend fun insertEntry(entry: Entry) {
        entryDao.insertEntry(entry)
    }
}
