package com.sherryyuan.wordy.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.DEFAULT_JUST_WRITE_PROJECT_TITLE
import com.sherryyuan.wordy.entitymodels.Entry
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val projectRepository: ProjectRepository,
    private val entryRepository: EntryRepository,
) : ViewModel() {

    private val wordCountInput = MutableStateFlow("")

    val state: StateFlow<HomeViewState> = createHomeState()

    fun onProjectSelected(project: Project) {
        viewModelScope.launch {
            projectRepository.updateSelectedProject(project.id)
        }
    }

    fun onWordCountInputChange(input: String) {
        if (input.matches(DIGITS_REGEX)) {
            wordCountInput.value = input
        }
    }

    fun onWordCountInputSubmit() {
        val entryWordCount = wordCountInput.value.toInt()
        viewModelScope.launch {
            val selectedProject = projectRepository.getSelectedProject()
                .first()
            if (selectedProject != null) {
                entryRepository.insertEntry(
                    Entry(
                        timestamp = System.currentTimeMillis(),
                        wordCount = entryWordCount,
                        projectId = selectedProject.id,
                    )
                )
            }
        }
    }

    private fun createHomeState(): StateFlow<HomeViewState> {
        return combine(
            projectRepository.getSelectedProject(),
            projectRepository.getProjects(),
            entryRepository.getEntriesForToday(),
            wordCountInput,
        ) { selectedProject, projects, entries, wordCountInput ->
            val projectTitle = when {
                selectedProject == null -> context.getString(R.string.no_selected_project_title)
                selectedProject.title == DEFAULT_JUST_WRITE_PROJECT_TITLE -> context.getString(R.string.just_writing_project_title)
                else -> selectedProject.title
            }
            val wordsToday = entries
                .filter { it.projectId == selectedProject?.id }
                .sumOf { it.wordCount }
            val dailyWordCountGoal = selectedProject?.dailyWordCountGoal ?: 0
            HomeViewState.Loaded(
                projectTitle = projectTitle,
                selectProjectOptions = projects,
                projectDescription = selectedProject?.description,
                currentWordCountInput = wordCountInput,
                wordsToday = wordsToday,
                dailyWordCountGoal = dailyWordCountGoal,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeViewState.Loading)
    }

    companion object {
        private val DIGITS_REGEX = Regex("^\\d+\$")
    }
}
