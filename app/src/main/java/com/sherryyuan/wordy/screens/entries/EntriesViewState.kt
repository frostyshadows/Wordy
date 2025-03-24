package com.sherryyuan.wordy.screens.entries

import java.time.LocalDate
import java.time.YearMonth

sealed class EntriesViewState(
    open val startYearMonth: YearMonth,
) {

    data class ListEntries(
        val isShowCurrentOnlyToggleVisible: Boolean,
        val showCurrentProjectOnly: Boolean,
        val monthlyEntries: List<MonthlyListEntries>,
        override val startYearMonth: YearMonth,
    ) : EntriesViewState(startYearMonth) {
        data class MonthlyListEntries(
            // eg. January 2025
            val monthHeaderText: String,
            val dailyEntries: List<DailyEntry>
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
        // eg. January 1
        val dateText: String,
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
