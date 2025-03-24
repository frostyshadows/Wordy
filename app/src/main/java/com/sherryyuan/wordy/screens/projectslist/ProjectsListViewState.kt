package com.sherryyuan.wordy.screens.projectslist

import androidx.annotation.StringRes
import com.sherryyuan.wordy.entitymodels.Project

data class ProjectsListViewState(
    val sections: List<ProjectsListSection>,
)

data class ProjectsListSection(
    @StringRes val titleRes: Int,
    val projectsWithWordCount: List<Pair<Project, Int>>,
)
