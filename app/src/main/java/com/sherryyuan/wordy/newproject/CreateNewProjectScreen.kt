package com.sherryyuan.wordy.newproject

import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.navigation.NavDestination
import com.sherryyuan.wordy.newproject.CreateNewProjectViewState.NewProjectGoal
import com.sherryyuan.wordy.ui.theme.SectionSpacer

@Composable
fun CreateNewProjectScreen(
    navController: NavHostController,
    viewModel: CreateNewProjectViewModel = hiltViewModel<CreateNewProjectViewModel>(),
) {

    val viewState by viewModel.state.collectAsState()

    LaunchedEffect(viewState.state) {
        if (viewState.state == CreateNewProjectViewState.State.SUBMITTED) {
            navController.navigate(NavDestination.Home) // TODO handle back navigation
        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (viewState.state) {
                CreateNewProjectViewState.State.EDITING_INFO -> {
                    ProjectInfoEditor(
                        viewState = viewState,
                        onTitleChange = viewModel::setTitle,
                        onDescriptionChange = viewModel::setDescription,
                        onNextClick = viewModel::continueToEditGoal,
                        onGoalTypeSelected = viewModel::setGoalType,
                    )
                }

                CreateNewProjectViewState.State.EDITING_WORD_COUNT_GOAL,
                CreateNewProjectViewState.State.SUBMITTING_WORD_COUNT_GOAL,
                -> WordCountGoalEditor(
                    viewState = viewState,
                    onWordCountChange = viewModel::updateWordCount,
                    onSubmitClick = viewModel::saveProject,
                )

                CreateNewProjectViewState.State.EDITING_DEADLINE_GOAL,
                CreateNewProjectViewState.State.SUBMITTING_DEADLINE_GOAL,
                -> TODO()

                CreateNewProjectViewState.State.SUBMITTED -> Unit
            }
        }
}

@Composable
private fun ColumnScope.ProjectInfoEditor(
    viewState: CreateNewProjectViewState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onGoalTypeSelected: (NewProjectGoal) -> Unit,
    onNextClick: () -> Unit,
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
        onValueChange = {
            onTitleChange(it)
        },
    )
    SectionSpacer()

    Text(stringResource(R.string.new_project_description))
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = viewState.description.orEmpty(),
        onValueChange = {
            onDescriptionChange(it)
        },
    )
    SectionSpacer()

    Text(stringResource(R.string.new_project_goal_type))
    SectionSpacer(heightDp = 12)
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

    Spacer(modifier = Modifier.weight(1f))

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

@Composable
private fun ColumnScope.WordCountGoalEditor(
    viewState: CreateNewProjectViewState,
    onWordCountChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
) {
    Text(viewState.title)
    SectionSpacer(

    )
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
