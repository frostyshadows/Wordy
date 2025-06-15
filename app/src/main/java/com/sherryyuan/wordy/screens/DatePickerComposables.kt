package com.sherryyuan.wordy.screens

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.utils.toEpochMillis
import com.sherryyuan.wordy.utils.toLocalDate
import java.time.LocalDate
import java.util.Calendar
import java.util.TimeZone

@Composable
fun StartDatePickerRow(
    startDate: LocalDate,
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
            initialDate = startDate,
            onDateSelected = onDateSelected,
        )
    }
}

@Composable
fun EndDatePickerRow(
    targetEndDate: LocalDate,
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
            initialDate = targetEndDate,
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
