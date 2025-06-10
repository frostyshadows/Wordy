package com.sherryyuan.wordy.screens.home

import java.time.LocalDate

sealed interface HomeViewState {
    data object Loading : HomeViewState

    data class Loaded(
        val projectTitle: String,
        val projectDescription: String?,
        val currentWordCountInput: String,
        val wordsToday: Int,
        // Used for drawing target line in cumulative goal charts
        val initialWordCountGoal: Int,
        // Same as initialWordCountGoal for daily goals.
        val adjustedWordCountGoal: Int,
        val selectedDisplayedChartRange: DisplayedChartRange,
        val chartWordCounts: Map<LocalDate, Int>,
    ) : HomeViewState

    enum class DisplayedChartRange {
        WEEK,
        MONTH,
        ALL_TIME,
        PROJECT_WITH_DEADLINE,
    }
}
