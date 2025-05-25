package com.sherryyuan.wordy.screens.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.yearMonth
import com.sherryyuan.wordy.entitymodels.DailyEntry
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import com.sherryyuan.wordy.screens.entries.EntriesViewState.CalendarEntries.DailyCalendarEntry
import com.sherryyuan.wordy.screens.entries.EntriesViewState.CalendarEntryProgress
import com.sherryyuan.wordy.screens.entries.EntriesViewState.ListEntries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EntriesViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val entryRepository: EntryRepository,
) : ViewModel() {

    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    private val dayFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

    private val selectedViewMode = MutableStateFlow(EntriesViewMode.LIST)
    private val showCurrentProjectOnly = MutableStateFlow(false)

    val state: StateFlow<EntriesViewState> = createEntriesState()

    fun onShowCurrentProjectOnlyToggle() {
        showCurrentProjectOnly.value = !showCurrentProjectOnly.value
    }

    fun onListViewClick() {
        selectedViewMode.value = EntriesViewMode.LIST
    }

    fun onCalendarViewClick() {
        selectedViewMode.value = EntriesViewMode.CALENDAR
    }

    private fun createEntriesState(): StateFlow<EntriesViewState> {
        return combine(
            selectedViewMode,
            showCurrentProjectOnly,
            entryRepository.getEntries(),
            projectRepository.getProjects(),
            projectRepository.getSelectedProject(),
        ) { mode, showCurrentProjectOnly, entries, projects, selectedProject ->
            when (mode) {
                EntriesViewMode.LIST -> {
                    entries.toListEntries(
                        projects,
                        selectedProject,
                        showCurrentProjectOnly,
                    )
                }

                EntriesViewMode.CALENDAR -> entries
                    .filter { selectedProject?.id == it.projectId }
                    .toCalendarEntries(
                        projects,
                        selectedProject,
                    )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ListEntries(
                isShowCurrentOnlyToggleVisible = false,
                showCurrentProjectOnly = showCurrentProjectOnly.value,
                monthlyEntries = emptyList(),
                startYearMonth = YearMonth.now(),
            ),
        )
    }

    private fun List<DailyEntry>.toListEntries(
        projects: List<Project>,
        selectedProject: Project?,
        showCurrentProjectOnly: Boolean,
    ): ListEntries {
        val groupedByYearMonth = if (showCurrentProjectOnly) {
            filter { selectedProject?.id == it.projectId }
        } else {
            this
        }
            .sortedByDescending { it.date }
            .groupBy { it.date.yearMonth }

        val monthlyEntries = groupedByYearMonth.map { (yearMonth, entries) ->
            val dailyEntries = entries
                .map { entry ->
                    val projectTitle =
                        projects.firstOrNull { it.id == entry.projectId }?.title.orEmpty()
                    EntriesViewState.DailyEntry(
                        entry.date.format(dayFormatter),
                        entry.wordCount,
                        projectTitle,
                    )
                }

            ListEntries.MonthlyListEntries(
                monthHeaderText =  yearMonth.format(monthFormatter),
                dailyEntries = dailyEntries,
            )
        }
        return ListEntries(
            isShowCurrentOnlyToggleVisible = isNotEmpty() && projects.size > 1,
            showCurrentProjectOnly = showCurrentProjectOnly,
            monthlyEntries = monthlyEntries,
            startYearMonth = earliestYearMonth(),
        )
    }

    private fun List<DailyEntry>.toCalendarEntries(
        projects: List<Project>,
        selectedProject: Project?,
    ): EntriesViewState.CalendarEntries {
        val sortedDailyEntries = sortedBy { it.date }
        val calendarDailyEntries = sortedDailyEntries.mapIndexed { index, dailyEntry ->
                val wordCountGoal = selectedProject?.goal?.initialDailyWordCount ?: 0
                val wordsCurrentDay = dailyEntry.wordCount
                val previousDayEntry = sortedDailyEntries.getOrNull(index - 1)
                    ?.takeIf {
                        it.date == dailyEntry.date.minusDays(1)
                    }
                val wordsPreviousDay = previousDayEntry?.wordCount ?: 0
                val nextDayEntry = sortedDailyEntries.getOrNull(index + 1)
                    ?.takeIf {
                        it.date == dailyEntry.date.plusDays(1)
                    }
                val wordsNextDay = nextDayEntry?.wordCount ?: 0

                val isGoalAchievedCurrentDay = wordsCurrentDay >= wordCountGoal
                val isGoalAchievedPreviousDay = wordsPreviousDay >= wordCountGoal
                val isGoalAchievedNextDay = wordsNextDay >= wordCountGoal
                val progress = when {
                    !isGoalAchievedCurrentDay -> CalendarEntryProgress.GoalProgress(
                        wordsCurrentDay.toFloat() / wordCountGoal
                    )

                    isGoalAchievedCurrentDay && isGoalAchievedPreviousDay && isGoalAchievedNextDay -> CalendarEntryProgress.GoalAchievedStreakMiddle
                    isGoalAchievedCurrentDay && isGoalAchievedPreviousDay -> CalendarEntryProgress.GoalAchievedStreakEnd
                    isGoalAchievedCurrentDay && isGoalAchievedNextDay -> CalendarEntryProgress.GoalAchievedStreakStart
                    else -> CalendarEntryProgress.GoalAchieved
                }
                DailyCalendarEntry(
                    date = dailyEntry.date,
                    progress = progress,
                    entry = EntriesViewState.DailyEntry(
                        dateText = dailyEntry.date.format(dayFormatter),
                        wordCount = dailyEntry.wordCount,
                        projectTitle = projects.firstOrNull { it.id == dailyEntry.projectId }?.title.orEmpty(),
                    ),
                )
            }

        return EntriesViewState.CalendarEntries(
            dailyWordCountGoal = selectedProject?.goal?.initialDailyWordCount ?: 0,
            startYearMonth = earliestYearMonth(),
            dailyEntries = calendarDailyEntries,
        )
    }

    private fun List<DailyEntry>.earliestYearMonth(): YearMonth {
        val earliestDate = minOfOrNull { it.date } ?: LocalDate.now()
        return earliestDate.yearMonth
    }

    enum class EntriesViewMode {
        LIST, CALENDAR
    }
}
