package com.sherryyuan.wordy.screens.projectslist

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.DailyEntry
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProjectsListViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val entryRepository: EntryRepository,
) : ViewModel() {

    val state: StateFlow<ProjectsListViewState> = createProjectsListState()

    private fun createProjectsListState(): StateFlow<ProjectsListViewState> {
        return combine(
            projectRepository.getSelectedProject(),
            projectRepository.getProjects(),
            entryRepository.getEntries(),

            ) { selectedProject, projects, entries ->
            val unsortedInProgressSection = createSectionForStatus(
                ProjectStatus.IN_PROGRESS,
                projects,
                entries,
            )
            // move selected project to top of list
            val partitionedProjects = unsortedInProgressSection.projectsWithWordCount
                .partition { it.first.id == selectedProject?.id }
            val inProgressSection = unsortedInProgressSection.copy(
                projectsWithWordCount = partitionedProjects.first + partitionedProjects.second
            )
            val notStartedSection = createSectionForStatus(
                ProjectStatus.NOT_STARTED,
                projects,
                entries,
            )
            val onHoldSection = createSectionForStatus(
                ProjectStatus.ON_HOLD,
                projects,
                entries,
            )
            val completedSection = createSectionForStatus(
                ProjectStatus.COMPLETED,
                projects,
                entries,
            )
            ProjectsListViewState(
                sections = listOf(
                    inProgressSection,
                    notStartedSection,
                    onHoldSection,
                    completedSection,
                )
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ProjectsListViewState(emptyList()))
    }

    private fun createSectionForStatus(
        status: ProjectStatus,
        allProjects: List<Project>,
        allEntries: List<DailyEntry>
    ): ProjectsListSection {
        val projectsWithWordCount = allProjects
            .filter { it.status == status }
            .map { project ->
                val wordCount = allEntries
                    .filter { it.projectId == project.id }
                    .sumOf { it.wordCount }
                Pair(project, wordCount)
            }
        return ProjectsListSection(getStatusLabel(status), projectsWithWordCount)
    }
}

@StringRes
fun getStatusLabel(status: ProjectStatus): Int =
    when (status) {
        ProjectStatus.NOT_STARTED -> R.string.not_started_status_label
        ProjectStatus.IN_PROGRESS -> R.string.in_progress_status_label
        ProjectStatus.ON_HOLD -> R.string.on_hold_status_label
        ProjectStatus.COMPLETED -> R.string.completed_status_label
    }

fun getStatusColor(status: ProjectStatus): Color =
    when (status) {
        ProjectStatus.NOT_STARTED -> Color.Gray
        ProjectStatus.IN_PROGRESS -> Color.Blue
        ProjectStatus.ON_HOLD -> Color.Yellow
        ProjectStatus.COMPLETED -> Color.Green
    }
