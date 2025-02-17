package com.sherryyuan.wordy.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // timestamp in millis at start of day
    val timestamp: Long,
    val wordCount: Int,
    val projectId: Long,
)
