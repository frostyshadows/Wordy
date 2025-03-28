package com.sherryyuan.wordy.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sherryyuan.wordy.utils.getDaysBetween
import com.squareup.moshi.JsonClass

const val DEFAULT_JUST_WRITE_PROJECT_ID = 31415926L

@Entity
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String?,
    val goal: Goal,
    val status: ProjectStatus,
)

enum class ProjectStatus {
    NOT_STARTED,
    IN_PROGRESS,
    ON_HOLD,
    COMPLETED,
}

sealed interface Goal {

    val dailyWordCount: Int

    @JsonClass(generateAdapter = true)
    data class DailyWordCountGoal(
        override val dailyWordCount: Int,
    ) : Goal

    @JsonClass(generateAdapter = true)
    data class DeadlineGoal(
        val targetTotalWordCount: Int,
        val startDateMillis: Long,
        val targetEndDateMillis: Long,
    ) : Goal {
        override val dailyWordCount: Int
            get() {
                val days = getDaysBetween(startDateMillis, targetEndDateMillis)
                return (targetTotalWordCount / days).toInt()
            }
    }
}
