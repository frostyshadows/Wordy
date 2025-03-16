package com.sherryyuan.wordy.home

sealed interface HomeViewState {
    data object Loading : HomeViewState

    data class Loaded(
        val projectTitle: String,
        val projectDescription: String?,
        val currentWordCountInput: String,
        val wordsToday: Int,
        val dailyWordCountGoal: Int,
        val selectedDisplayedChartRange: DisplayedChartRange,
        val chartWordCounts: Map<Long, Int>, // date timestamp to word count
    ) : HomeViewState

    enum class DisplayedChartRange {
        WEEK,
        MONTH,
        ALL_TIME,
        PROJECT_WITH_DEADLINE,
    }
}
