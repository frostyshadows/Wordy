package com.sherryyuan.wordy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sherryyuan.wordy.models.Entry
import com.sherryyuan.wordy.models.Project

@Database(entities = [Entry::class, Project::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun projectDao(): ProjectDao
}