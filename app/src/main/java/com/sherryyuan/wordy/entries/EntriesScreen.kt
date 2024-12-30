package com.sherryyuan.wordy.entries

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import com.sherryyuan.wordy.R
import java.time.YearMonth

@Composable
fun EntriesScreen(
    viewModel: EntriesViewModel = hiltViewModel<EntriesViewModel>()
) {
    val viewState by viewModel.state.collectAsState()

    val currentYearMonth = remember { YearMonth.now() }
    val daysOfWeek = remember { daysOfWeek() }

    val calendarState = rememberCalendarState(
        startMonth = viewState.startYearMonth,
        endMonth = currentYearMonth,
        firstVisibleMonth = currentYearMonth,
        firstDayOfWeek = daysOfWeek.first(),
    )

    Column {
        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                onClick = { viewModel.onListViewClick() },
                selected = viewState is EntriesViewState.ListEntries,
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.list_icon),
                        contentDescription = null,
                    )
                }
            ) {
                Text(stringResource(R.string.entries_list_label))
            }
            SegmentedButton(
                onClick = { viewModel.onCalendarViewClick() },
                selected = viewState is EntriesViewState.CalendarEntries,
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.calendar_icon),
                        contentDescription = null,
                    )
                }
            ) {
                Text(stringResource(R.string.entries_calendar_label))
            }
        }

        when (val state = viewState) {
            is EntriesViewState.ListEntries -> {
                ListEntries(
                    entriesState = state,
                    onShowCurrentProjectClick = { viewModel.onShowCurrentProjectOnlyToggle() },
                )
            }

            is EntriesViewState.CalendarEntries -> {
                calendarState.startMonth
                CalendarEntries(
                    entriesState = state,
                    calendarState = calendarState,
                    currentYearMonth = currentYearMonth,
                    daysOfWeek = daysOfWeek,
                )

            }
        }
    }
}
