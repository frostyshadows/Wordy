package com.sherryyuan.wordy.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.DEFAULT_JUST_WRITE_PROJECT_TITLE
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val state: StateFlow<HomeViewState> = createHomeState()

    fun onProjectSelected(project: Project) {
        viewModelScope.launch {
            projectRepository.updateSelectedProject(project.id)
        }
    }

    private fun createHomeState(): StateFlow<HomeViewState> {
        return combine(
            projectRepository.getSelectedProject(),
            projectRepository.getProjects(),
        ) { selectedProject, projects ->
            println("Testing in createHomeState, selectedProject title = ${selectedProject?.title}, selectedProject id = ${selectedProject?.id}")
            val projectTitle = when {
                selectedProject == null -> context.getString(R.string.no_selected_project_title)
                selectedProject.title == DEFAULT_JUST_WRITE_PROJECT_TITLE -> context.getString(R.string.just_writing_project_title)
                else -> selectedProject.title
            }
            HomeViewState.Loaded(
                projectTitle = projectTitle,
                selectProjectOptions = projects,
                projectDescription = selectedProject?.description,
                wordsToday = 0,
                dailyWordCountGoal = 0,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeViewState.Loading)
    }
}
