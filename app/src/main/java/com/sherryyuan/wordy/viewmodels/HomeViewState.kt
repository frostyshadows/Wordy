package com.sherryyuan.wordy.viewmodels

import com.sherryyuan.wordy.entitymodels.Project

sealed interface HomeViewState {
    data object Loading : HomeViewState

    data class Loaded(
        val projectTitle: String,
        val selectProjectOptions: List<Project>,
        val projectDescription: String?,
        val currentWordCountInput: String,
        val wordsToday: Int,
        val dailyWordCountGoal: Int,
    ) : HomeViewState
}
