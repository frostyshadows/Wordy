package com.sherryyuan.wordy.entries

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.entitymodels.Entry
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.sherryyuan.wordy.entries.EntriesViewState.ListEntries

@HiltViewModel
class EntriesViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val entryRepository: EntryRepository,
) : ViewModel() {

    private val selectedViewMode = MutableStateFlow(EntriesViewMode.LIST)

    val state: StateFlow<EntriesViewState> = createEntriesState()

    fun onListViewClick() {
        selectedViewMode.value = EntriesViewMode.LIST
    }

    fun onCalendarViewClick() {
        selectedViewMode.value = EntriesViewMode.CALENDAR
    }

    private fun createEntriesState(): StateFlow<EntriesViewState> {
        return combine(
            selectedViewMode,
            entryRepository.getEntries(),
            projectRepository.getProjects(),
        ) { mode, entries, projects ->
            when (mode) {
                EntriesViewMode.LIST -> entries.toListEntriesViewState(projects)
                EntriesViewMode.CALENDAR -> EntriesViewState.CalendarEntries
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ListEntries(emptyList()),
        )
    }

    private fun List<Entry>.toListEntriesViewState(projects: List<Project>): ListEntries {
        val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val dayFormatter = SimpleDateFormat("MMM d", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

        // Group entries by month
        val groupedByMonth = groupBy { entry ->
            val calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            monthFormatter.format(calendar.time)
        }

        // Map to MonthlyListEntries
        val monthlyEntries = groupedByMonth.map { (month, monthEntries) ->
            val groupedByDay = monthEntries.groupBy { entry ->
                val calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
                dayFormatter.format(calendar.time)
            }

            val dailyEntries = groupedByDay.map { (day, dayEntries) ->
                val sortedDayEntries = dayEntries.sortedBy { it.timestamp }
                ListEntries.DailyListEntries(
                    dateText = day,
                    entries = sortedDayEntries.map { entry ->
                        val projectTitle =
                            projects.firstOrNull { it.id == entry.projectId }?.title.orEmpty()
                        ListEntries.DailyListEntry(
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
        return ListEntries(monthlyEntries)
    }

    enum class EntriesViewMode {
        LIST, CALENDAR
    }
}
