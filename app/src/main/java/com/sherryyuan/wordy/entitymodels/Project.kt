package com.sherryyuan.wordy.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sherryyuan.wordy.utils.projectDaysCount
import com.squareup.moshi.JsonClass
import java.time.LocalDate

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
        val startDate: LocalDate,
        val targetEndDate: LocalDate,
    ) : Goal {
        override val initialDailyWordCount: Int
            get() {
                val days = projectDaysCount(startDate, targetEndDate)
                return targetTotalWordCount / days
            }

        fun adjustedDailyWordCount(existingEntries: List<DailyEntry> = emptyList()): Int {
            val remainingDays = projectDaysCount(
                startDate = LocalDate.now().coerceAtLeast(startDate),
                endDate = targetEndDate,
            )
            val existingWordCount = existingEntries.filter { it.date < LocalDate.now() }
                .sumOf { it.wordCount }
            val remainingWordCount = targetTotalWordCount - existingWordCount
            return remainingWordCount / remainingDays
        }
    }
}
