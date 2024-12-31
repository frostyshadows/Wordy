package com.sherryyuan.wordy.entries

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.yearMonth
import com.sherryyuan.wordy.entitymodels.Entry
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entries.EntriesViewState.CalendarEntries.DailyCalendarEntries
import com.sherryyuan.wordy.entries.EntriesViewState.ListEntries
import com.sherryyuan.wordy.entries.EntriesViewState.CalendarEntriesProgress
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EntriesViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val entryRepository: EntryRepository,
) : ViewModel() {

    private val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val dayFormatter = SimpleDateFormat("MMM d", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

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
                EntriesViewMode.LIST -> entries.toListEntries(
                    projects,
                    selectedProject,
                    showCurrentProjectOnly,
                )

                EntriesViewMode.CALENDAR -> entries.toCalendarEntries(
                    projects,
                    selectedProject,
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ListEntries(
                showCurrentProjectOnly = false,
                monthlyEntries = emptyList(),
                startYearMonth = YearMonth.now(),
            ),
        )
    }

    private fun List<Entry>.toListEntries(
        projects: List<Project>,
        selectedProject: Project?,
        showCurrentProjectOnly: Boolean,
    ): ListEntries {
        val groupedByMonth = groupBy { entry ->
            val calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            monthFormatter.format(calendar.time)
        }

        val monthlyEntries = groupedByMonth.map { (month, monthEntries) ->
            val groupedByDay = monthEntries.groupBy { entry ->
                val calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
                dayFormatter.format(calendar.time)
            }

            val dailyEntries = groupedByDay.map { (day, dayEntries) ->
                val sortedDayEntries = dayEntries
                    .filter {
                        if (showCurrentProjectOnly) {
                            selectedProject?.id == it.projectId
                        } else {
                            true
                        }
                    }
                    .sortedBy { it.timestamp }
                ListEntries.DailyListEntries(
                    dateText = day,
                    entries = sortedDayEntries.map { entry ->
                        val projectTitle =
                            projects.firstOrNull { it.id == entry.projectId }?.title.orEmpty()
                        EntriesViewState.DailyEntry(
                            timeText = timeFormatter.format(Date(entry.timestamp)),
                            wordCount = entry.wordCount,
                            projectTitle = projectTitle,
                        )
                    }
                )
            }.sortedByDescending { it.entries.firstOrNull()?.timeText }

            ListEntries.MonthlyListEntries(
                monthHeaderText = month,
                dailyEntries = dailyEntries,
            )
        }.sortedByDescending { monthly ->
            monthFormatter.parse(monthly.monthHeaderText).time
        }
        return ListEntries(
            showCurrentProjectOnly = showCurrentProjectOnly,
            monthlyEntries = monthlyEntries,
            startYearMonth = earliestYearMonth(),
        )
    }

    private fun List<Entry>.toCalendarEntries(
        projects: List<Project>,
        selectedProject: Project?,
    ): EntriesViewState.CalendarEntries {
        val groupedEntries = groupBy { entry ->
            Instant.ofEpochMilli(entry.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        val dailyEntriesWithoutProgress = groupedEntries.map { (date, entriesForDate) ->
            DailyCalendarEntries(
                date = date,
                progress = CalendarEntriesProgress.GoalAchieved,
                entries = entriesForDate.map { entry ->
                    EntriesViewState.DailyEntry(
                        timeText = timeFormatter.format(Date(entry.timestamp)),
                        wordCount = entry.wordCount,
                        projectTitle = projects.firstOrNull { it.id == entry.projectId }?.title.orEmpty()
                    )
                }
            )
        }
        val dailyEntriesWithProgress =
            dailyEntriesWithoutProgress.mapIndexed { index, dailyCalendarEntries ->
                val wordCountGoal = selectedProject?.goal?.dailyWordCount ?: 0
                val wordsCurrentDay = dailyCalendarEntries.entries.sumOf { it.wordCount }
                val previousDayEntries = dailyEntriesWithoutProgress.getOrNull(index - 1)
                val wordsPreviousDay = previousDayEntries?.entries?.sumOf { it.wordCount } ?: 0
                val nextDayEntries = dailyEntriesWithoutProgress.getOrNull(index + 1)
                val wordsNextDay = nextDayEntries?.entries?.sumOf { it.wordCount } ?: 0

                val isGoalAchievedCurrentDay = wordsCurrentDay >= wordCountGoal
                val isGoalAchievedPreviousDay = wordsPreviousDay >= wordCountGoal
                val isGoalAchievedNextDay = wordsNextDay >= wordCountGoal
                val progress = when {
                    !isGoalAchievedCurrentDay -> CalendarEntriesProgress.GoalProgress(
                        wordsCurrentDay.toFloat() / wordCountGoal
                    )

                    isGoalAchievedCurrentDay && isGoalAchievedPreviousDay && isGoalAchievedNextDay -> CalendarEntriesProgress.GoalAchievedStreakMiddle
                    isGoalAchievedCurrentDay && isGoalAchievedPreviousDay -> CalendarEntriesProgress.GoalAchievedStreakEnd
                    isGoalAchievedCurrentDay && isGoalAchievedNextDay -> CalendarEntriesProgress.GoalAchievedStreakStart
                    else -> CalendarEntriesProgress.GoalAchieved
                }
                dailyCalendarEntries.copy(progress = progress)
            }

        return EntriesViewState.CalendarEntries(
            dailyWordCountGoal = selectedProject?.goal?.dailyWordCount ?: 0,
            startYearMonth = earliestYearMonth(),
            dailyEntries = dailyEntriesWithProgress,
        )
    }

    private fun List<Entry>.earliestYearMonth(): YearMonth {
        val earliestTimestamp = minOf { it.timestamp }
        val earliestLocalDate =
            Instant.ofEpochMilli(earliestTimestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        return earliestLocalDate.yearMonth
    }

    enum class EntriesViewMode {
        LIST, CALENDAR
    }
}
