package com.sherryyuan.wordy.entries

sealed interface EntriesViewState {

    data class ListEntries(
        val monthlyEntries: List<MonthlyListEntries>,
    ) : EntriesViewState {
        data class MonthlyListEntries(
            // eg. January 2025
            val monthHeaderText: String,
            val dailyEntries: List<DailyListEntries>
        )

        data class DailyListEntries(
            // eg. Jan 1
            val dateText: String,
            val entries: List<DailyListEntry>,
        )

        data class DailyListEntry(
            val timeText: String,
            val wordCount: Int,
            val projectTitle: String,
        )
    }

    // TODO use https://github.com/kizitonwose/Calendar?tab=readme-ov-file?
    data object CalendarEntries : EntriesViewState
}
