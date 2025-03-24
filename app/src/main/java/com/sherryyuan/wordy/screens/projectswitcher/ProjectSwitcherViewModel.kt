package com.sherryyuan.wordy.screens.projectswitcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectSwitcherViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
) : ViewModel() {

    val state: StateFlow<ProjectSwitcherViewState> = createProjectSwitcherState()

    fun onProjectSelected(project: Project) {
        viewModelScope.launch {
            projectRepository.updateSelectedProject(project.id)
        }
    }

    private fun createProjectSwitcherState(): StateFlow<ProjectSwitcherViewState> {
        return combine(
            projectRepository.getSelectedProject(),
            projectRepository.getProjects(),
        ) { selectedProject, projects ->
            val options = projects
                .filter { it.status == ProjectStatus.IN_PROGRESS }
                .map {
                    ProjectSwitcherOption.ProjectWithSelection(
                        project = it,
                        isSelected = it.id == selectedProject?.id
                    )
            } + ProjectSwitcherOption.NewProject
            ProjectSwitcherViewState(options)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ProjectSwitcherViewState(emptyList()))
    }
}
