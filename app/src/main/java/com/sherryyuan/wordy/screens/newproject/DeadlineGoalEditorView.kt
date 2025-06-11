package com.sherryyuan.wordy.screens.newproject

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.screens.newproject.CreateNewProjectViewState.NewProjectGoal
import com.sherryyuan.wordy.ui.theme.VerticalSpacer
import com.sherryyuan.wordy.utils.toEpochMillis
import com.sherryyuan.wordy.utils.toLocalDate
import java.time.LocalDate
import java.util.Calendar
import java.util.TimeZone

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
        goal = goal,
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
        goal = goal,
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
private fun StartDatePickerRow(
    goal: NewProjectGoal.Deadline,
    onDateSelected: (Long) -> Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            text = stringResource(R.string.start_date_label),
        )
        DatePickerInputField(
            modifier = Modifier.weight(2f),
            initialDate = goal.projectStartDate,
            onDateSelected = onDateSelected,
        )
    }
}

@Composable
private fun EndDatePickerRow(
    goal: NewProjectGoal.Deadline,
    onDateSelected: (Long) -> Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            text = stringResource(R.string.end_date_label),
        )
        DatePickerInputField(
            modifier = Modifier.weight(2f),
            initialDate = goal.targetProjectEndDate,
            onDateSelected = onDateSelected,
        )
    }
}

@Composable
private fun DatePickerInputField(
    modifier: Modifier = Modifier,
    initialDate: LocalDate,
    onDateSelected: (Long) -> Boolean, // return true if selected date is valid
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var showModal by remember { mutableStateOf(false) }

    TextField(
        value = selectedDate.toString(),
        onValueChange = { },
        placeholder = { selectedDate.toString() },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModal(
            initialSelectedDateMillis = selectedDate.toEpochMillis(),
            onDateSelected = { date ->
                date?.let {
                    // date picker gives date in GMT time zone
                    val selectedDateMillis = it - TimeZone.getDefault().rawOffset
                    val shouldUpdateSelection = onDateSelected(selectedDateMillis)
                    if (shouldUpdateSelection) {
                        selectedDate = selectedDateMillis.toLocalDate()
                    }
                }
            },
            onDismiss = { showModal = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    initialSelectedDateMillis: Long,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        // 15-year range, should be enough
        yearRange = IntRange(start = currentYear - 5, endInclusive = currentYear + 9)
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.confirm_label))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_label))
            }
        }
    ) {
        DatePicker(state = datePickerState)
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
