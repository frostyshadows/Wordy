package com.sherryyuan.wordy.home

import com.sherryyuan.wordy.entitymodels.Project

sealed interface HomeViewState {
    data object Loading : HomeViewState

    data class Loaded(
        val projectTitle: String,
        val projectDescription: String?,
        val currentWordCountInput: String,
        val wordsToday: Int,
        val dailyWordCountGoal: Int,
    ) : HomeViewState
}
