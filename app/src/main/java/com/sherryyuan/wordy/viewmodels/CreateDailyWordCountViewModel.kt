package com.sherryyuan.wordy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDailyWordCountViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _wordCountInput = MutableStateFlow("500") // TODO create a state class with word count and button state
    val wordCountInput: StateFlow<String> = _wordCountInput

    fun updateWordCount(input: String) {
        if (input.length <= MAX_DIGITS && (input.isEmpty() || input.matches(DIGITS_REGEX))) {
            _wordCountInput.value = input
        }
    }

    fun saveWordCountProject() {
        val wordCount = _wordCountInput.value.toInt()
        val newProject = Project(
            title = "default", // TODO only insert if default project doesn't already exist
            description = null,
            targetTotalWordCount = null,
            projectStartTime = null,
            targetProjectEndTime = null,
            dailyWordCountGoal = wordCount,
            status = ProjectStatus.IN_PROGRESS,
        )
        viewModelScope.launch {
            projectRepository.insertProject(newProject)
        }
    }

    companion object {
        private val DIGITS_REGEX = Regex("^\\d+\$")
        private const val MAX_DIGITS = 6
    }
}
