package com.sherryyuan.wordy.screens.projectswitcher

import com.sherryyuan.wordy.entitymodels.Project

data class ProjectSwitcherViewState(
    val options: List<ProjectSwitcherOption>,
)

sealed interface ProjectSwitcherOption {
    data class ProjectWithSelection(
        val project: Project,
        val isSelected: Boolean,
    ) : ProjectSwitcherOption

    data object NewProject : ProjectSwitcherOption
}
