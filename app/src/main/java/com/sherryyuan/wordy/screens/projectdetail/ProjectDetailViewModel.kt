package com.sherryyuan.wordy.screens.projectdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.navigation.WordyNavDestination.Companion.NAV_ARG_PROJECT_ID
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val entryRepository: EntryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val projectId = savedStateHandle.get<Long>(NAV_ARG_PROJECT_ID)
    private val isEditing = MutableStateFlow(false)

    // initialization order matters here since projectId is used in state creation
    val state: StateFlow<ProjectDetailViewState> = createProjectDetailState()

    fun updateIsEditing(isEditing: Boolean) {
        this.isEditing.value = isEditing
    }

    fun saveProject(
        title: String,
        status: ProjectStatus,
        description: String?,
        goal: Goal,
    ) {
        projectId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val updatedGoal = if (goal.initialDailyWordCount > 0) {
                    goal
                } else {
                    null
                }
                projectRepository.updateProject(
                    id = it,
                    title = title,
                    status = status,
                    description = description,
                    goal = updatedGoal,
                )
            }
        }
    }

    private fun createProjectDetailState(): StateFlow<ProjectDetailViewState> {
        projectId ?: return MutableStateFlow(ProjectDetailViewState.Loading)
        return combine(
            projectRepository.getProjectsById(projectId),
            entryRepository.getEntries(),
            isEditing,
        ) { project, entries, isEditing ->
            val wordCount = entries
                .filter { it.projectId == project.id }
                .sumOf { it.wordCount }
            ProjectDetailViewState.Loaded(
                projectWithWordCount = Pair(project, wordCount),
                isEditing = isEditing,
            )
        }.stateIn(
            viewModelScope, SharingStarted.Eagerly,
            ProjectDetailViewState.Loading,
        )
    }
}
