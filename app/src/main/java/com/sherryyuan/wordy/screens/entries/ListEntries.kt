package com.sherryyuan.wordy.screens.entries

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.ui.HorizontalSpacer
import com.sherryyuan.wordy.ui.VerticalSpacer
import com.sherryyuan.wordy.ui.theme.WordyTheme
import java.time.YearMonth

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListEntries(
    entriesState: EntriesViewState.ListEntries,
    onShowCurrentProjectClick: () -> Unit,
    contentPaddingBottom: Dp = 0.dp,
) {
    val monthsCollapsedState = remember(entriesState) {
        entriesState.monthlyEntries.map { false }.toMutableStateList()
    }

    if (entriesState.isShowCurrentOnlyToggleVisible) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = entriesState.showCurrentProjectOnly,
                onCheckedChange = { onShowCurrentProjectClick() }
            )
            Text(stringResource(R.string.show_selected_project))
        }
    }
    LazyColumn(
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = contentPaddingBottom)
    ) {
        entriesState.monthlyEntries.forEachIndexed { i, entries ->
            val isCollapsed = monthsCollapsedState[i]
            stickyHeader {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(bottom = 4.dp)
                        .fillMaxWidth()
                        .clickable { monthsCollapsedState[i] = !isCollapsed }
                        .animateItem()
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = entries.monthHeaderText,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Icon(
                        if (isCollapsed) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                    )
                }
            }
            if (!isCollapsed) {
                items(entries.dailyEntries, key = { it.dateText }) {
                    DailyEntry(
                        entry = it,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .animateItem(),
                    )
                }
            }
            item {
                VerticalSpacer(heightDp = 8)
            }
        }
    }
}

@Composable
private fun DailyEntry(
    entry: EntriesViewState.DailyEntry,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
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
