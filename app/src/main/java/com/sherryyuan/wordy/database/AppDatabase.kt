package com.sherryyuan.wordy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sherryyuan.wordy.entitymodels.DailyEntry
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.SelectedProject

@Database(
    entities = [
        DailyEntry::class,
        Project::class,
        SelectedProject::class,
    ],
    version = 1,
)
@TypeConverters(GoalTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dailyEntryDao(): DailyEntryDao
    abstract fun projectDao(): ProjectDao
    abstract fun selectedProjectDao(): SelectedProjectDao
}
