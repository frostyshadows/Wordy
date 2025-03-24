package com.sherryyuan.wordy.screens.projectdetail

import com.sherryyuan.wordy.entitymodels.Project

sealed interface ProjectDetailViewState {
    data object Loading : ProjectDetailViewState

    data class Loaded(
        val projectWithWordCount: Pair<Project, Int>,
    ) : ProjectDetailViewState
}
