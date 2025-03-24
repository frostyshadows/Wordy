package com.sherryyuan.wordy.screens.entries

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sherryyuan.wordy.R
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
                    DailyEntry(it)
                }
            }
        }
    }
}

@Composable
private fun DailyEntry(entry: EntriesViewState.DailyEntry) {
    Row {
        Text(entry.dateText)
        HorizontalSpacer()
        Text(stringResource(R.string.words_message, entry.wordCount))
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
                            EntriesViewState.DailyEntry(
                                dateText = "January 26",
                                wordCount = 200,
                                projectTitle = "Viridian",
                            ),
                            EntriesViewState.DailyEntry(
                                dateText = "January 10",
                                wordCount = 200,
                                projectTitle = "Viridian",
                            )
                        )
                    ),
                    EntriesViewState.ListEntries.MonthlyListEntries(
                        monthHeaderText = "December 2024",
                        dailyEntries = listOf(),
                    ),
                ),
                startYearMonth = YearMonth.now(),
            ),
            onShowCurrentProjectClick = {},
        )
    }
}
