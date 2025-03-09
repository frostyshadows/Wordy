package com.sherryyuan.wordy.newproject

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
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.newproject.CreateNewProjectViewState.NewProjectGoal
import com.sherryyuan.wordy.ui.theme.VerticalSpacer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ColumnScope.DeadlineGoalEditor(
    viewState: CreateNewProjectViewState,
    onWordCountChange: (String) -> Unit,
    onStartDateChange: (Long) -> Unit,
    onEndDateChange: (Long) -> Unit,
    onSubmitClick: () -> Unit,
) {
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

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            text = stringResource(R.string.start_date_label),
        )
        DatePickerInputField(
            modifier = Modifier.weight(2f),
            initialDateMillis = goal.projectStartDateMillis,
            onDateSelected = onStartDateChange,
        )
    }

    VerticalSpacer(8)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            text = stringResource(R.string.end_date_label),
        )
        DatePickerInputField(
            modifier = Modifier.weight(2f),
            initialDateMillis = goal.targetProjectEndDateMillis,
            onDateSelected = onEndDateChange,
        )
    }

    VerticalSpacer(8)

    Text(stringResource(R.string.deadline_word_count_to_reach_goal, goal.dailyWordCount))

//    Text(stringResource(R.string.current_word_count))
//    TextField(
//        value = "",
//        onValueChange = {
//            onWordsSoFarChange(it)
//        },
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//    )

    Spacer(modifier = Modifier.weight(1f))
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = viewState.state == CreateNewProjectViewState.State.EDITING_DEADLINE_GOAL &&
                viewState.goal.saveButtonEnabled,
        onClick = { onSubmitClick() }
    ) {
        Text(stringResource(R.string.confirm_label))
    }
}

@Composable
private fun DatePickerInputField(
    modifier: Modifier = Modifier,
    initialDateMillis: Long,
    onDateSelected: (Long) -> Unit
) {
    var selectedDateMillis by remember { mutableLongStateOf(initialDateMillis) }
    var showModal by remember { mutableStateOf(false) }

    TextField(
        value = convertMillisToDate(selectedDateMillis),
        onValueChange = { },
        placeholder = { Text(convertMillisToDate(selectedDateMillis)) },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDateMillis) {
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
            initialSelectedDateMillis = selectedDateMillis + TimeZone.getDefault().rawOffset,
            onDateSelected = { date ->
                date?.let {
                    // date picker gives date in GMT time zone
                    selectedDateMillis = it - TimeZone.getDefault().rawOffset
                    onDateSelected(selectedDateMillis)
                }
            },
            onDismiss = { showModal = false }
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    initialSelectedDateMillis: Long,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis,
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
