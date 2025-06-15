package com.sherryyuan.wordy.screens.newproject

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.screens.EndDatePickerRow
import com.sherryyuan.wordy.screens.StartDatePickerRow
import com.sherryyuan.wordy.screens.newproject.CreateNewProjectViewState.NewProjectGoal
import com.sherryyuan.wordy.ui.VerticalSpacer
import com.sherryyuan.wordy.utils.toLocalDate
import java.time.LocalDate

@Composable
fun ColumnScope.DeadlineGoalEditor(
    viewState: CreateNewProjectViewState,
    onWordCountChange: (String) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit,
    onSubmitClick: () -> Unit,
) {
    var modalMessage: String? by remember { mutableStateOf(null) }

    val goal = viewState.goal as NewProjectGoal.Deadline

    Text(viewState.title)
    VerticalSpacer()
    Text(stringResource(R.string.deadline_word_count_goal_header))

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            value = goal.targetTotalWordCount,
            onValueChange = {
                onWordCountChange(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(stringResource(R.string.total))
    }

    VerticalSpacer(8)

    val startDateWarning = stringResource(R.string.start_date_warning)
    StartDatePickerRow(
        startDate = goal.projectStartDate,
        onDateSelected = { startDateMillis ->
            val startDate = startDateMillis.toLocalDate()
            if (startDate < goal.targetProjectEndDate) {
                onStartDateChange(startDate)
            } else {
                modalMessage = startDateWarning
            }
            startDate < goal.targetProjectEndDate
        },
    )

    VerticalSpacer(8)

    val endDateWarning = stringResource(R.string.end_date_warning)
    EndDatePickerRow(
        targetEndDate = goal.targetProjectEndDate,
        onDateSelected = { endDateMillis ->
            val endDate = endDateMillis.toLocalDate()
            if (endDate > goal.projectStartDate) {
                onEndDateChange(endDate)
            } else {
                modalMessage = endDateWarning
            }
            endDate > goal.projectStartDate
        },
    )

    VerticalSpacer(8)

    StyledWordCountText(goal.dailyWordCount)

    Spacer(modifier = Modifier.weight(1f))
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = viewState.state == CreateNewProjectViewState.State.EDITING_DEADLINE_GOAL &&
                viewState.goal.saveButtonEnabled,
        onClick = { onSubmitClick() }
    ) {
        Text(stringResource(R.string.confirm_label))
    }

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
}

@Composable
private fun StyledWordCountText(dailyWordCount: Int) {
    val wordCountMessage =
        stringResource(R.string.deadline_word_count_to_reach_goal, dailyWordCount)
    val wordCountString = dailyWordCount.toString()
    val boldStart = wordCountMessage.indexOf(wordCountString)
    val spanStyles = listOf(
        AnnotatedString.Range(
            SpanStyle(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary),
            start = boldStart,
            end = boldStart + wordCountString.length,
        )
    )
    Text(AnnotatedString(text = wordCountMessage, spanStyles = spanStyles))
}
