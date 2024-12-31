package com.sherryyuan.wordy.entries

import java.time.LocalDate
import java.time.YearMonth

sealed class EntriesViewState(
    open val startYearMonth: YearMonth,
) {

    data class ListEntries(
        val showCurrentProjectOnly: Boolean,
        val monthlyEntries: List<MonthlyListEntries>,
        override val startYearMonth: YearMonth,
    ) : EntriesViewState(startYearMonth) {
        data class MonthlyListEntries(
            // eg. January 2025
            val monthHeaderText: String,
            val dailyEntries: List<DailyListEntries>
        )

        data class DailyListEntries(
            // eg. Jan 1
            val dateText: String,
            val entries: List<DailyEntry>,
        )
    }

    data class CalendarEntries(
        val dailyWordCountGoal: Int,
        val dailyEntries: List<DailyCalendarEntries>,
        override val startYearMonth: YearMonth,
    ) : EntriesViewState(startYearMonth) {

        data class DailyCalendarEntries(
            val date: LocalDate,
            val progress: CalendarEntriesProgress,
            val entries: List<DailyEntry>,
        )
    }

    data class DailyEntry(
        val timeText: String,
        val wordCount: Int,
        val projectTitle: String,
    )

    sealed interface CalendarEntriesProgress {
        data object GoalAchieved : CalendarEntriesProgress
        data object GoalAchievedStreakStart : CalendarEntriesProgress
        data object GoalAchievedStreakMiddle : CalendarEntriesProgress
        data object GoalAchievedStreakEnd : CalendarEntriesProgress
        data class GoalProgress(val percentAchieved: Float) : CalendarEntriesProgress
    }
}
