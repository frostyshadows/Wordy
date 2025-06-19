package com.sherryyuan.wordy.screens.projectdetail

import android.text.Html
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.text.toSpanned
import androidx.hilt.navigation.compose.hiltViewModel
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.ui.VerticalSpacer
import com.sherryyuan.wordy.utils.TOP_BAR_ANIMATION_KEY
import com.sherryyuan.wordy.utils.toAnnotatedString

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ProjectDetailScreen(
    onNavigateToProjectsList: () -> Unit,
    topBarAnimatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ProjectDetailViewModel = hiltViewModel<ProjectDetailViewModel>(),
) {
    val viewState by viewModel.state.collectAsState()
    val isEditing by remember(viewState) {
        mutableStateOf((viewState as? ProjectDetailViewState.Loaded)?.isEditing ?: false)
    }
    var editedTitle by remember(viewState) {
        mutableStateOf((viewState as? ProjectDetailViewState.Loaded)?.projectWithWordCount?.first?.title.orEmpty())
    }
    var editedStatus by remember(viewState) {
        mutableStateOf(
            (viewState as? ProjectDetailViewState.Loaded)?.projectWithWordCount?.first?.status
                ?: ProjectStatus.IN_PROGRESS
        )
    }
    var editedDescription by remember(viewState) {
        mutableStateOf((viewState as? ProjectDetailViewState.Loaded)?.projectWithWordCount?.first?.description)
    }
    var editedGoal by remember(viewState) {
        mutableStateOf(
            (viewState as? ProjectDetailViewState.Loaded)?.projectWithWordCount?.first?.goal
                ?: Goal.DailyWordCountGoal(500)
        )
    }

    Scaffold(
        topBar = {
            ProjectDetailTopAppBar(
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = TOP_BAR_ANIMATION_KEY),
                        animatedVisibilityScope = topBarAnimatedVisibilityScope,
                    )
                    .skipToLookaheadSize(),
                projectTitle = editedTitle,
                isEditing = isEditing,
                onIsEditingChange = { isEditing ->
                    viewModel.updateIsEditing(isEditing)
                    if (!isEditing) {
                        viewModel.saveProject(
                            title = editedTitle,
                            status = editedStatus,
                            description = editedDescription,
                            goal = editedGoal,
                        )
                    }
                },
                onTitleUpdate = { editedTitle = it }
            )
        }
    ) { contentPadding ->
        when (viewState) {
            is ProjectDetailViewState.Loaded -> LoadedProjectDetails(
                modifier = Modifier.padding(contentPadding),
                title = editedTitle,
                status = editedStatus,
                description = editedDescription,
                goal = editedGoal,
                isEditing = isEditing,
                onStatusUpdate = { editedStatus = it },
                onDescriptionUpdate = { editedDescription = it },
                onGoalUpdate = { editedGoal = it },
                onProjectDelete = {
                    viewModel.deleteProject()
                    onNavigateToProjectsList()
                },
            )

            ProjectDetailViewState.Loading -> {}
        }
    }
}

@Composable
private fun LoadedProjectDetails(
    status: ProjectStatus,
    title: String,
    description: String?,
    goal: Goal,
    isEditing: Boolean,
    onStatusUpdate: (ProjectStatus) -> Unit,
    onDescriptionUpdate: (String) -> Unit,
    onGoalUpdate: (Goal) -> Unit,
    onProjectDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        ProjectStatusRow(
            status = status,
            isEditing = isEditing,
            onStatusUpdate = onStatusUpdate,
        )
        ProjectDescriptionSection(
            description = description,
            isEditing = isEditing,
            onDescriptionUpdate = onDescriptionUpdate,
        )
        ProjectDetailGoalSection(
            goal = goal,
            isEditing = isEditing,
            onGoalUpdate = onGoalUpdate,
        )

        if (isEditing) {
            VerticalSpacer()
            ProjectDetailDeleteButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                projectTitle = title,
                onProjectDelete = onProjectDelete,
            )
        }
    }
}

@Composable
private fun ProjectDescriptionSection(
    description: String?,
    isEditing: Boolean,
    onDescriptionUpdate: (String) -> Unit,
) {
    Column {
        Text(stringResource(R.string.new_project_description))
        AnimatedContent(
            targetState = isEditing,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220, delayMillis = 60)) +
                        scaleIn(
                            initialScale = 0.92f,
                            animationSpec = tween(220, delayMillis = 60)
                        ))
                    .togetherWith(fadeOut(animationSpec = tween(120)))
            },
        ) { animatedIsEditing ->
            if (animatedIsEditing) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    value = description.orEmpty(),
                    placeholder = {
                        Text(stringResource(R.string.edit_project_add_description))
                    },
                    onValueChange = { onDescriptionUpdate(it) },
                )
            } else {
                description?.let {
                    Text(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectDetailDeleteButton(
    projectTitle: String,
    onProjectDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showModal by remember { mutableStateOf(false) }
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        onClick = { showModal = true },
    ) {
        Text(stringResource(R.string.delete_project_button))
    }

    if (showModal) {
        val modalText = Html.fromHtml(
            stringResource(R.string.delete_project_modal_text, projectTitle)
        ).toAnnotatedString()
        BasicAlertDialog(
            onDismissRequest = { showModal = false },
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 360.0.dp)
                    .background(
                        color = AlertDialogDefaults.containerColor,
                        shape = AlertDialogDefaults.shape,
                    )
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 24.dp, top = 24.dp, end = 24.dp),
                    text = modalText,
                )
                VerticalSpacer()
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 6.dp, bottom = 8.dp)
                ) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.secondary,
                        ),
                        onClick = { showModal = false },
                    ) {
                        Text(stringResource(R.string.cancel_label))
                    }
                    TextButton(
                        colors = ButtonDefaults.textButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                        onClick = { onProjectDelete() },
                    ) {
                        Text(stringResource(R.string.confirm_label))
                    }
                }
            }
        }
    }
}
