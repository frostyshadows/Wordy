package com.sherryyuan.wordy.entries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entries.EntriesViewState.ListEntries.DailyListEntries
import com.sherryyuan.wordy.ui.theme.HorizontalSpacer
import com.sherryyuan.wordy.ui.theme.WordyTheme
import java.time.YearMonth

@Composable
fun ListEntries(
    entriesState: EntriesViewState.ListEntries,
    onShowCurrentProjectClick: () -> Unit,
) {
    val monthsCollapsedState = remember(entriesState) {
        entriesState.monthlyEntries.map { false }.toMutableStateList()
    }

    if (entriesState.isShowCurrentOnlyToggleVisible) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = entriesState.showCurrentProjectOnly,
                onCheckedChange = { onShowCurrentProjectClick() }
            )
            Text(stringResource(R.string.show_selected_project))
        }
    }
    LazyColumn {
        entriesState.monthlyEntries.forEachIndexed { i, entries ->
            val isCollapsed = monthsCollapsedState[i]
            item(entries.monthHeaderText) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { monthsCollapsedState[i] = !isCollapsed }
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = entries.monthHeaderText,
                    )
                    Icon(
                        if (isCollapsed) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                    )
                }
            }
            if (!isCollapsed) {
                items(entries.dailyEntries) {
                    DailyEntries(it)
                }
            }
        }
    }
}

@Composable
private fun DailyEntries(entries: DailyListEntries) {
    var isDayCollapsed by remember(entries) {
        mutableStateOf(true)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isDayCollapsed = !isDayCollapsed }
    ) {
        Icon(
            if (isDayCollapsed) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
            contentDescription = null,
        )
        Text(entries.dateText)
        HorizontalSpacer()
        val wordCount = entries.entries.sumOf { it.wordCount }
        Text(stringResource(R.string.words_message, wordCount))
    }
    if (!isDayCollapsed) {
        Column {
            entries.entries.forEach { entry ->
                Row {
                    Text(entry.timeText)
                    HorizontalSpacer()
                    Text(stringResource(R.string.words_message, entry.wordCount))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListEntriesPreview() {
    WordyTheme {
        ListEntries(
            EntriesViewState.ListEntries(
                isShowCurrentOnlyToggleVisible = true,
                showCurrentProjectOnly = true,
                monthlyEntries = listOf(
                    EntriesViewState.ListEntries.MonthlyListEntries(
                        monthHeaderText = "January 2025",
                        dailyEntries = listOf(
                            DailyListEntries(
                                dateText = "Jan 26",
                                entries = listOf(
                                    EntriesViewState.DailyEntry(
                                        timeText = "10:00am",
                                        wordCount = 200,
                                        projectTitle = "Viridian",
                                    ),
                                    EntriesViewState.DailyEntry(
                                        timeText = "2:00pm",
                                        wordCount = 200,
                                        projectTitle = "Viridian",
                                    )
                                )
                            ),
                            DailyListEntries(
                                dateText = "Jan 10",
                                entries = listOf()
                            )
                        )
                    ),
                    EntriesViewState.ListEntries.MonthlyListEntries(
                        monthHeaderText = "December 2024",
                        dailyEntries = listOf(),
                    )
                ),
                startYearMonth = YearMonth.now(),
            ),
            onShowCurrentProjectClick = {},
        )
    }
}
