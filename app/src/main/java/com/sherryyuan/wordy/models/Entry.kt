package com.sherryyuan.wordy.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Entry(
    val timestamp: Long,
    val wordCount: Int,
    val projectId: Int?,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
