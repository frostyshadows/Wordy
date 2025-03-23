package com.sherryyuan.wordy.projectlist

import androidx.annotation.StringRes
import com.sherryyuan.wordy.entitymodels.Project

data class ProjectListViewState(
    val sections: List<ProjectListSection>,
)

data class ProjectListSection(
    @StringRes val titleRes: Int,
    val projectsWithWordCount: List<Pair<Project, Int>>,
)
