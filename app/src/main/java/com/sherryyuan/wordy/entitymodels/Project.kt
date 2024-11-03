package com.sherryyuan.wordy.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey

// Placeholder title when users want to set a daily word count goal without a specific project.
// When displaying the project in-app, show R.string.just_writing_project_title instead.
const val DEFAULT_JUST_WRITE_PROJECT_TITLE = "default_just_write_project"

@Entity
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String?,
    val targetTotalWordCount: Int?,
    val projectStartTime: Long?,
    val targetProjectEndTime: Long?,
    val dailyWordCountGoal: Int,
    val status: ProjectStatus,
)

enum class ProjectStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
}
