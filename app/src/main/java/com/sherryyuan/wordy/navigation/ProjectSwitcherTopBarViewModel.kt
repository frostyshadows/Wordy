package com.sherryyuan.wordy.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProjectSwitcherTopBarViewModel @Inject constructor(
    projectRepository: ProjectRepository,
) : ViewModel() {

    val selectedProject: StateFlow<Project?> = projectRepository
        .getSelectedProject()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
