package com.sherryyuan.wordy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.entitymodels.DEFAULT_JUST_WRITE_PROJECT_TITLE
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDefaultProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateDefaultProjectViewState())
    val state: StateFlow<CreateDefaultProjectViewState> = _state

    fun updateWordCount(input: String) {
        if (input.length <= MAX_DIGITS && (input.isEmpty() || input.matches(DIGITS_REGEX))) {
            _state.value = _state.value.copy(wordCount = input)
        }
    }

    fun saveDefaultProject() {
        val wordCount = _state.value.wordCount.toInt()
        val newProject = Project(
            title = DEFAULT_JUST_WRITE_PROJECT_TITLE,
            description = null,
            targetTotalWordCount = null,
            projectStartTime = null,
            targetProjectEndTime = null,
            dailyWordCountGoal = wordCount,
            status = ProjectStatus.IN_PROGRESS,
        )
        viewModelScope.launch {
            _state.value = _state.value.copy(state = CreateDefaultProjectViewState.State.SUBMITTING)
            val newProjectId = projectRepository.insertProject(newProject)
            if (projectRepository.getSelectedProject().first() == null) {
                println("Testing in updateSelectedProject, newProject title = ${newProject.title}, newProject id = ${newProject.id}")
                projectRepository.updateSelectedProject(newProjectId)
            }
            _state.value = _state.value.copy(state = CreateDefaultProjectViewState.State.SUBMITTED)
        }
    }

    companion object {
        private val DIGITS_REGEX = Regex("^\\d+\$")
        private const val MAX_DIGITS = 6
    }
}
