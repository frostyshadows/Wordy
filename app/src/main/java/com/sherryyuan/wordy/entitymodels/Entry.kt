package com.sherryyuan.wordy.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val wordCount: Int,
    val projectId: Long,
)
