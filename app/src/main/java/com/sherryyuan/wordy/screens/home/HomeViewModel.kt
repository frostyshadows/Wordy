package com.sherryyuan.wordy.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import com.sherryyuan.wordy.screens.home.HomeViewState.DisplayedChartRange.ALL_TIME
import com.sherryyuan.wordy.screens.home.HomeViewState.DisplayedChartRange.MONTH
import com.sherryyuan.wordy.screens.home.HomeViewState.DisplayedChartRange.PROJECT_WITH_DEADLINE
import com.sherryyuan.wordy.screens.home.HomeViewState.DisplayedChartRange.WEEK
import com.sherryyuan.wordy.utils.DIGITS_REGEX
import com.sherryyuan.wordy.utils.fromPastDays
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val projectRepository: ProjectRepository,
    private val entryRepository: EntryRepository,
) : ViewModel() {

    private val wordCountInput = MutableStateFlow("")
    private val displayedChartRange =
        MutableStateFlow(HomeViewState.DisplayedChartRange.WEEK)

    init {
        viewModelScope.launch {
            projectRepository.getSelectedProject().collect { project ->
                if (project?.goal is Goal.DailyWordCountGoal && displayedChartRange.value == PROJECT_WITH_DEADLINE) {
                    displayedChartRange.value = WEEK
                } else if (project?.goal is Goal.DeadlineGoal && displayedChartRange.value != PROJECT_WITH_DEADLINE) {
                    displayedChartRange.value = PROJECT_WITH_DEADLINE
                }
                // TODO delete this when it's not needed
                project?.id?.let { populateDailyEntries(it) } // for testing purposes
            }
        }
    }

    val state: StateFlow<HomeViewState> = createHomeState()

    fun setWordCount(wordCount: String) {
        if (wordCount.matches(DIGITS_REGEX)) {
            wordCountInput.value = wordCount
        }
    }

    fun onWordCountInputSubmit() {
        val entryWordCount = wordCountInput.value.toInt()
        viewModelScope.launch {
            val selectedProject = projectRepository.getSelectedProject()
                .first()
            if (selectedProject != null) {
                entryRepository.insertEntry(
                    date = LocalDate.now(),
                    wordCount = entryWordCount,
                    projectId = selectedProject.id,
                    updateWordCountStrategy = EntryRepository.UpdateWordCountStrategy.ADD,
                )
            }
            wordCountInput.value = ""
        }
    }

    private fun createHomeState(): StateFlow<HomeViewState> {
        return combine(
            projectRepository.getSelectedProject(),
            entryRepository.getEntries(),
            displayedChartRange,
            wordCountInput,
        ) { selectedProject, entries, chartRange, wordCountInput ->
            val projectTitle = when {
                selectedProject == null -> context.getString(R.string.no_selected_project_title)
                else -> selectedProject.title
            }
            val selectedProjectEntries = entries.filter { it.projectId == selectedProject?.id }
            val wordsToday = selectedProjectEntries
                .fromPastDays(1)
                .sumOf { it.wordCount }
            val dailyWordCountGoal = when (val goal = selectedProject?.goal) {
                is Goal.DailyWordCountGoal -> goal.initialDailyWordCount
                is Goal.DeadlineGoal ->
                    goal.adjustedDailyWordCount(selectedProjectEntries)

                null -> 0
            }
            val chartWordCounts: Map<LocalDate, Int> = when (displayedChartRange.value) {
                WEEK ->
                    selectedProjectEntries.fromPastDays(7)
                        .associate { it.date to it.wordCount }

                MONTH ->
                    selectedProjectEntries.fromPastDays(30)
                        .associate { it.date to it.wordCount }

                ALL_TIME -> TODO()
                PROJECT_WITH_DEADLINE -> {
                    val existingEntriesDates = selectedProjectEntries.map { it.date }
                    val existingEntriesCumulativeWordCounts = selectedProjectEntries.map {
                        it.wordCount
                    }.runningReduce { sum, count ->
                        sum + count
                    }
                    val existingEntries = existingEntriesDates
                        .zip(existingEntriesCumulativeWordCounts)
                        .toMap()
                    val futureEntries = mapOf<LocalDate, Int>() // TODO
                    existingEntries + futureEntries
                }
            }
            HomeViewState.Loaded(
                projectTitle = projectTitle,
                projectDescription = selectedProject?.description,
                currentWordCountInput = wordCountInput,
                wordsToday = wordsToday,
                dailyWordCountGoal = dailyWordCountGoal,
                selectedDisplayedChartRange = chartRange, // TODO handle selections
                chartWordCounts = chartWordCounts,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeViewState.Loading)
    }

    // populate a few random entries for testing
    private suspend fun populateDailyEntries(projectId: Long) {
        val now = LocalDate.now()
        listOf(
            now.minusDays(33) to 300,
            now.minusDays(31) to 500,
            now.minusDays(13) to 1,
            now.minusDays(10) to 1201,
            now.minusDays(9) to 501,
            now.minusDays(8) to 501,
            now.minusDays(7) to 50,
            now.minusDays(5) to 1500,
            now.minusDays(2) to 600,
        ).forEach { (date, wordCount) ->
            entryRepository.insertEntry(
                date = date,
                wordCount = wordCount,
                projectId = projectId,
                updateWordCountStrategy = EntryRepository.UpdateWordCountStrategy.REPLACE,
            )
        }
    }
}
