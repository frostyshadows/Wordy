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
        val dailyEntries: List<DailyCalendarEntry>,
        override val startYearMonth: YearMonth,
    ) : EntriesViewState(startYearMonth) {

        data class DailyCalendarEntry(
            val date: LocalDate,
            val entry: DailyEntry,
            val progress: CalendarEntryProgress,
        )
    }

    data class DailyEntry(
        // eg. January 1
        val dateText: String,
        val wordCount: Int,
        val projectTitle: String,
    )

    sealed interface CalendarEntryProgress {
        data object GoalAchieved : CalendarEntryProgress
        data object GoalAchievedStreakStart : CalendarEntryProgress
        data object GoalAchievedStreakMiddle : CalendarEntryProgress
        data object GoalAchievedStreakEnd : CalendarEntryProgress
        data class GoalProgress(val percentAchieved: Float) : CalendarEntryProgress
    }
}
