package com.sherryyuan.wordy.screens.projectdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.screens.EndDatePickerRow
import com.sherryyuan.wordy.screens.StartDatePickerRow
import com.sherryyuan.wordy.ui.VerticalSpacer
import com.sherryyuan.wordy.utils.toLocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ProjectDetailGoalSection(
    goal: Goal,
    isEditing: Boolean,
    onGoalUpdate: (Goal) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(stringResource(R.string.edit_project_goal_title))
        when (goal) {
            is Goal.DailyWordCountGoal -> {
                WordCountGoalContent(
                    goal = goal,
                    isEditing = isEditing,
                    onGoalUpdate = onGoalUpdate,
                )
            }

            is Goal.DeadlineGoal -> {
                DeadlineGoalContent(
                    goal = goal,
                    isEditing = isEditing,
                    onGoalUpdate = onGoalUpdate,
                )
            }
        }
    }
}

@Composable
private fun WordCountGoalContent(
    goal: Goal.DailyWordCountGoal,
    isEditing: Boolean,
    onGoalUpdate: (Goal) -> Unit,
) {
    if (isEditing) {
        Text(stringResource(R.string.want_to_write_header))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                value = goal.initialDailyWordCount.toString(),
                onValueChange = {
                    onGoalUpdate(goal.copy(initialDailyWordCount = it.toIntOrNull() ?: 0))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(stringResource(R.string.words_per_day))
        }
    } else {
        Text(
            stringResource(
                R.string.edit_project_words_per_day_template,
                goal.initialDailyWordCount,
            )
        )
    }
}

@Composable
private fun DeadlineGoalContent(
    goal: Goal.DeadlineGoal,
    isEditing: Boolean,
    onGoalUpdate: (Goal) -> Unit,
) {
    val dateTimeFormatter = DateTimeFormatter
        .ofPattern("MMM d, yyyy")
        .withZone(ZoneId.systemDefault())
    var modalMessage: String? by remember { mutableStateOf(null) }
    if (isEditing) {
        Text(stringResource(R.string.deadline_word_count_goal_header))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                value = goal.targetTotalWordCount.toString(),
                onValueChange = {
                    onGoalUpdate(goal.copy(targetTotalWordCount = it.toIntOrNull() ?: 0))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(stringResource(R.string.total))
        }

        VerticalSpacer(8)
        val startDateWarning = stringResource(R.string.start_date_warning)
        StartDatePickerRow(
            startDate = goal.startDate,
            onDateSelected = { startDateMillis ->
                val startDate = startDateMillis.toLocalDate()
                if (startDate < goal.targetEndDate) {
                    onGoalUpdate(goal.copy(startDate = startDate))
                } else {
                    modalMessage = startDateWarning
                }
                startDate < goal.targetEndDate
            },
        )

        VerticalSpacer(8)

        val endDateWarning = stringResource(R.string.end_date_warning)
        EndDatePickerRow(
            targetEndDate = goal.targetEndDate,
            onDateSelected = { endDateMillis ->
                val endDate = endDateMillis.toLocalDate()
                if (endDate > goal.startDate) {
                    onGoalUpdate(goal.copy(targetEndDate = endDate))
                } else {
                    modalMessage = endDateWarning
                }
                endDate > goal.startDate
            },
        )
        modalMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { modalMessage = null },
                title = { Text(message) },
                confirmButton = {
                    TextButton(
                        onClick = { modalMessage = null }
                    ) {
                        Text(stringResource(R.string.confirm_label))
                    }
                }
            )
        }
    } else {
        Text(
            stringResource(
                R.string.edit_project_deadline_goal_template,
                goal.targetTotalWordCount,
                goal.startDate.format(dateTimeFormatter),
                goal.targetEndDate.format(dateTimeFormatter),
            )
        )
    }
}
