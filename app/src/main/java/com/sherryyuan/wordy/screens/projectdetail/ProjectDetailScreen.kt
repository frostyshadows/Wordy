package com.sherryyuan.wordy.screens.projectdetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.utils.TOP_BAR_ANIMATION_KEY

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ProjectDetailScreen(
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
                status = editedStatus,
                description = editedDescription,
                goal = editedGoal,
                isEditing = isEditing,
                onStatusUpdate = { editedStatus = it },
                onDescriptionUpdate = { editedDescription = it },
                onGoalUpdate = { editedGoal = it },
            )

            ProjectDetailViewState.Loading -> {}
        }
    }
}

@Composable
private fun LoadedProjectDetails(
    status: ProjectStatus,
    description: String?,
    goal: Goal,
    isEditing: Boolean,
    onStatusUpdate: (ProjectStatus) -> Unit,
    onDescriptionUpdate: (String) -> Unit,
    onGoalUpdate: (Goal) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
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
