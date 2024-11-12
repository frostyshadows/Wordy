package com.sherryyuan.wordy.newproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.newproject.CreateNewProjectViewState.NewProjectGoal
import com.sherryyuan.wordy.newproject.CreateNewProjectViewState.State
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
class CreateNewProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val titleInput = MutableStateFlow("")
    private val descriptionInput = MutableStateFlow("")
    private val goal: MutableStateFlow<NewProjectGoal> = MutableStateFlow(NewProjectGoal.WordCount())
    private val currentState: MutableStateFlow<State> = MutableStateFlow(State.EDITING_INFO)

    val state: StateFlow<CreateNewProjectViewState> = createNewProjectState()

    fun setTitle(title: String) {
        titleInput.value = title
    }

    fun setDescription(description: String) {
        descriptionInput.value = description
    }

    fun setGoalType(goal: NewProjectGoal) {
        this.goal.value = goal
    }

    fun continueToEditGoal() {
        require(currentState.value == State.EDITING_INFO)
        currentState.value = if (goal.value is NewProjectGoal.WordCount) {
            State.EDITING_WORD_COUNT_GOAL
        } else {
            State.EDITING_DEADLINE_GOAL
        }
    }

    fun updateWordCount(input: String) {
        if (input.length > MAX_WORD_COUNT_DIGITS || !(input.isEmpty() || input.matches(DIGITS_REGEX))) return
        when (val currentGoal = goal.value) {
            is NewProjectGoal.WordCount -> {
                val updatedGoal = NewProjectGoal.WordCount(input)
                goal.value = updatedGoal
            }

            is NewProjectGoal.Deadline -> {
                val updatedGoal = currentGoal.copy(targetTotalWordCount = input)
                goal.value = updatedGoal
            }
        }
    }

    fun saveProject() {
        when (val goal = goal.value) {
            is NewProjectGoal.WordCount -> saveWordCountProject(goal)
            is NewProjectGoal.Deadline -> TODO()
            else -> error("Project cannot be saved")
        }
    }

    private fun saveWordCountProject(goal: NewProjectGoal.WordCount) {
        val newProject = Project(
            title = titleInput.value,
            description = descriptionInput.value,
            goal = Goal.DailyWordCountGoal(goal.wordCount.toInt()),
            status = ProjectStatus.IN_PROGRESS,
        )
        viewModelScope.launch {
            currentState.value = State.SUBMITTING_WORD_COUNT_GOAL
            // TODO handle duplicate titles
            val newProjectId = projectRepository.insertProject(newProject)
            if (projectRepository.getSelectedProject().first() == null) {
                projectRepository.updateSelectedProject(newProjectId)
            }
            currentState.value = State.SUBMITTED
        }
    }

    private fun createNewProjectState(): StateFlow<CreateNewProjectViewState> {
        return combine(
            titleInput,
            descriptionInput,
            goal,
            currentState,
        ) { title, description, goal, state ->
            CreateNewProjectViewState(
                title = title,
                description = description,
                goal = goal,
                state = state,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, CreateNewProjectViewState())
    }
}