package com.sherryyuan.wordy.projectlist

import androidx.annotation.StringRes
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
class ProjectListViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val entryRepository: EntryRepository,
) : ViewModel() {

    val state: StateFlow<ProjectListViewState> = createProjectListState()

    fun onProjectSelected(project: Project) {
//        viewModelScope.launch {
//            projectRepository.updateSelectedProject(project.id)
//        }
    }

    private fun createProjectListState(): StateFlow<ProjectListViewState> {
        return combine(
            projectRepository.getSelectedProject(),
            projectRepository.getProjects(),
            entryRepository.getEntries(),

            ) { selectedProject, projects, entries ->
            val unsortedInProgressSection = createSectionForStatus(
                R.string.in_progress_status_label,
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
                R.string.not_started_status_label,
                ProjectStatus.NOT_STARTED,
                projects,
                entries,
            )
            val onHoldSection = createSectionForStatus(
                R.string.on_hold_status_label,
                ProjectStatus.ON_HOLD,
                projects,
                entries,
            )
            val completedSection = createSectionForStatus(
                R.string.completed_status_label,
                ProjectStatus.COMPLETED,
                projects,
                entries,
            )
            ProjectListViewState(
                sections = listOf(
                    inProgressSection,
                    notStartedSection,
                    onHoldSection,
                    completedSection,
                )
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ProjectListViewState(emptyList()))
    }

    private fun createSectionForStatus(
        @StringRes titleRes: Int,
        status: ProjectStatus,
        allProjects: List<Project>,
        allEntries: List<DailyEntry>
    ): ProjectListSection {
        val projectsWithWordCount = allProjects
            .filter { it.status == status }
            .map { project ->
                val wordCount = allEntries
                    .filter { it.projectId == project.id }
                    .sumOf { it.wordCount }
                Pair(project, wordCount)
            }
        return ProjectListSection(titleRes, projectsWithWordCount)
    }
}
