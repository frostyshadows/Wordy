package com.sherryyuan.wordy.screens.entries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.ui.topAndSideContentPadding
import java.time.YearMonth

@Composable
fun EntriesScreen(
    topBar: @Composable () -> Unit,
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

    Scaffold(
        topBar = { topBar() }
    ) { contentPadding ->
        Column(modifier = Modifier.topAndSideContentPadding(contentPadding)) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
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
                        contentPaddingBottom = contentPadding.calculateBottomPadding(),
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
}
