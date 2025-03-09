package com.sherryyuan.wordy.defaultproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.entitymodels.DEFAULT_JUST_WRITE_PROJECT_ID
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.repositories.ProjectRepository
import com.sherryyuan.wordy.utils.DIGITS_REGEX
import com.sherryyuan.wordy.utils.MAX_WORD_COUNT_DIGITS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDefaultProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val wordCountInput = MutableStateFlow("")
    private val currentState: MutableStateFlow<CreateDefaultProjectViewState.State> =
        MutableStateFlow(CreateDefaultProjectViewState.State.EDITING)

    val state: StateFlow<CreateDefaultProjectViewState> = createDefaultProjectState()

    fun setWordCount(wordCount: String) {
        if (wordCount.length <= MAX_WORD_COUNT_DIGITS && (wordCount.isEmpty() || wordCount.matches(DIGITS_REGEX))) {
            wordCountInput.value = wordCount
        }
    }

    fun saveDefaultProject(title: String) {
        val wordCount = wordCountInput.value.toInt()
        val newProject = Project(
            id = DEFAULT_JUST_WRITE_PROJECT_ID,
            title = title,
            description = null,
            goal = Goal.DailyWordCountGoal(wordCount),
            status = ProjectStatus.IN_PROGRESS,
        )
        viewModelScope.launch {
            currentState.value = CreateDefaultProjectViewState.State.SUBMITTING
            val newProjectId = projectRepository.insertProject(newProject)
            if (projectRepository.getSelectedProject().first() == null) {
                println("Testing in updateSelectedProject, newProject title = ${newProject.title}, newProject id = ${newProject.id}")
                projectRepository.updateSelectedProject(newProjectId)
            }
            currentState.value = CreateDefaultProjectViewState.State.SUBMITTED
        }
    }

    private fun createDefaultProjectState(): StateFlow<CreateDefaultProjectViewState> {
        return combine(
            wordCountInput,
            currentState,
        ) { wordCount, state ->
            CreateDefaultProjectViewState(
                wordCount = wordCount,
                state = state,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, CreateDefaultProjectViewState())
    }
}
