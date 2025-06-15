package com.sherryyuan.wordy.screens.newproject

import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.screens.newproject.CreateNewProjectViewState.NewProjectGoal
import com.sherryyuan.wordy.ui.VerticalSpacer

@Composable
fun ColumnScope.ProjectInfoEditor(
    viewState: CreateNewProjectViewState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onGoalTypeSelected: (NewProjectGoal) -> Unit,
    onNextClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .weight(1f)
            .padding(bottom = 8.dp)
    ) {
        Text(
            buildAnnotatedString {
                append(stringResource(R.string.new_project_title))
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append("*")
                }
            }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewState.title,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            onValueChange = {
                onTitleChange(it)
            },
        )
        VerticalSpacer()

        Text(stringResource(R.string.new_project_description))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewState.description.orEmpty(),
            onValueChange = {
                onDescriptionChange(it)
            },
        )
        VerticalSpacer()

        Text(stringResource(R.string.new_project_goal_type))
        VerticalSpacer(heightDp = 8)
        GoalOptionRadioButton(
            selected = viewState.goal is NewProjectGoal.WordCount,
            onSelect = { onGoalTypeSelected(NewProjectGoal.WordCount()) },
            labelRes = R.string.new_project_daily_goal_description,
        )
        GoalOptionRadioButton(
            selected = viewState.goal is NewProjectGoal.Deadline,
            onSelect = { onGoalTypeSelected(NewProjectGoal.Deadline()) },
            labelRes = R.string.new_project_deadline_goal_description,
        )
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = viewState.title.isNotBlank(),
        onClick = { onNextClick() }
    ) {
        Text(stringResource(R.string.next_label))
    }
}

@Composable
private fun GoalOptionRadioButton(
    selected: Boolean,
    onSelect: () -> Unit,
    @StringRes labelRes: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { onSelect() }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = (selected),
            onClick = { onSelect() },
            interactionSource = remember { MutableInteractionSource() }
        )
        Text(
            text = stringResource(labelRes),
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
