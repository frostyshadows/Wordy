package com.sherryyuan.wordy.newproject

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.newproject.CreateNewProjectViewState.NewProjectGoal
import com.sherryyuan.wordy.ui.theme.VerticalSpacer

@Composable
fun ColumnScope.WordCountGoalEditor(
    viewState: CreateNewProjectViewState,
    onWordCountChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
) {
    Text(viewState.title)
    VerticalSpacer()
    Text(stringResource(R.string.want_to_write_header))

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            value = (viewState.goal as NewProjectGoal.WordCount).wordCount,
            onValueChange = {
                onWordCountChange(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(stringResource(R.string.words_per_day))
    }
    Spacer(modifier = Modifier.weight(1f))
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = viewState.state == CreateNewProjectViewState.State.EDITING_WORD_COUNT_GOAL &&
                viewState.goal.saveButtonEnabled,
        onClick = { onSubmitClick() }
    ) {
        Text(stringResource(R.string.confirm_label))
    }
}
