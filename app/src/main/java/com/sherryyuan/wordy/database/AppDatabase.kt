package com.sherryyuan.wordy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sherryyuan.wordy.entitymodels.Entry
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.SelectedProject

@Database(entities = [Entry::class, Project::class, SelectedProject::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun projectDao(): ProjectDao
    abstract fun selectedProjectDao(): SelectedProjectDao
}
