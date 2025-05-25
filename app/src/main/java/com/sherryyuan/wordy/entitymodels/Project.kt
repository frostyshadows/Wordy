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

    // Word count goal stays the same for DailyWordCountGoal,
    // but needs to be adjusted for DeadlineGoal based on logged entries.
    val initialDailyWordCount: Int

    @JsonClass(generateAdapter = true)
    data class DailyWordCountGoal(
        override val initialDailyWordCount: Int,
    ) : Goal

    @JsonClass(generateAdapter = true)
    data class DeadlineGoal(
        val targetTotalWordCount: Int,
        val startDateMillis: Long,
        val targetEndDateMillis: Long,
    ) : Goal {
        override val initialDailyWordCount: Int
            get() {
                val days = getDaysBetween(startDateMillis, targetEndDateMillis)
                return (targetTotalWordCount / days).toInt()
            }

        fun adjustedDailyWordCount(existingEntries: List<DailyEntry> = emptyList()): Int {
            System.currentTimeMillis().coerceAtLeast(startDateMillis)
            val remainingDays = getDaysBetween(
                System.currentTimeMillis().coerceAtLeast(startDateMillis),
                targetEndDateMillis,
            )
            val existingWordCount = existingEntries.sumOf { it.wordCount }
            val remainingWordCount = targetTotalWordCount - existingWordCount
            return (remainingWordCount / remainingDays).toInt()
        }
    }
}
