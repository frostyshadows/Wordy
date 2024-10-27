package com.sherryyuan.wordy.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Project(
    val title: String,
    val description: String?,
    val targetTotalWordCount: Int,
    val projectStartTime: Long,
    val targetProjectEndTime: Long?,
    val dailyWordCountGoal: Int,
    val status: ProjectStatus,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

enum class ProjectStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
}
