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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.screens.projectslist.getStatusColor
import com.sherryyuan.wordy.screens.projectslist.getStatusLabelRes
import com.sherryyuan.wordy.ui.theme.HorizontalSpacer
import com.sherryyuan.wordy.utils.TOP_BAR_ANIMATION_KEY

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ProjectDetailScreen(
    topBarAnimatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ProjectDetailViewModel = hiltViewModel<ProjectDetailViewModel>(),
) {
    val viewState by viewModel.state.collectAsState()
    val projectTitle =
        (viewState as? ProjectDetailViewState.Loaded)?.projectWithWordCount?.first?.title

    var editingField by remember {
        mutableStateOf(EditingField.NONE)
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
                projectTitle = projectTitle.orEmpty(),
                isEditing = editingField == EditingField.TITLE,
                onIsEditingChange = { isEditing ->
                    editingField = if (isEditing) {
                        EditingField.TITLE
                    } else {
                        EditingField.NONE
                    }
                },
                onTitleUpdate = { viewModel.onTitleUpdate(it) }
            )
        }
    ) { contentPadding ->
        when (val state = viewState) {
            is ProjectDetailViewState.Loaded -> LoadedProjectDetails(
                modifier = Modifier.padding(contentPadding),
                project = state.projectWithWordCount.first,
                isEditingStatus = editingField == EditingField.STATUS,
                onIsEditingStatusChange = { isEditing ->
                    editingField = if (isEditing) {
                        EditingField.STATUS
                    } else {
                        EditingField.NONE
                    }
                },
                onStatusUpdate = { viewModel.onStatusUpdate(it) },
            )

            ProjectDetailViewState.Loading -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectDetailTopAppBar(
    projectTitle: String,
    isEditing: Boolean,
    onIsEditingChange: (Boolean) -> Unit,
    onTitleUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editedTitle by remember(projectTitle) {
        mutableStateOf(projectTitle)
    }
    TopAppBar(
        modifier = modifier,
        title = {
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
                            .padding(start = 24.dp),
                        value = editedTitle,
                        singleLine = true,
                        onValueChange = { editedTitle = it },
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp),
                        textAlign = TextAlign.Center,
                        text = projectTitle,
                    )

                }
            }
        },
        actions = {
            if (isEditing) {
                IconButton(
                    onClick = {
                        onTitleUpdate(editedTitle)
                        onIsEditingChange(false)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Outlined.Check,
                        contentDescription = stringResource(R.string.save_label),
                    )
                }
            } else {
                IconButton(
                    onClick = { onIsEditingChange(true) }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit_label),
                    )
                }
            }
        },
    )
}

@Composable
private fun LoadedProjectDetails(
    project: Project,
    isEditingStatus: Boolean,
    onIsEditingStatusChange: (Boolean) -> Unit,
    onStatusUpdate: (ProjectStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        ProjectStatusRow(
            project = project,
            isEditing = isEditingStatus,
            onIsEditingChange = onIsEditingStatusChange,
            onStatusUpdate = onStatusUpdate,
        )
        ProjectDescriptionSection(project)
    }
}

@Composable
private fun ProjectStatusRow(
    project: Project,
    isEditing: Boolean,
    onIsEditingChange: (Boolean) -> Unit,
    onStatusUpdate: (ProjectStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusChipHeight = measureTextHeight(
        stringResource(getStatusLabelRes(project.status)),
        LocalTextStyle.current,
    ) + 4.dp
    Row(modifier = modifier.heightIn(min = statusChipHeight)) {
        Text(stringResource(R.string.project_detail_status_label))
        HorizontalSpacer(4)
        if (isEditing) {
            Box(modifier = Modifier.wrapContentSize()) {
                DropdownMenu(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    expanded = true,
                    onDismissRequest = { onIsEditingChange(false) }
                ) {
                    StatusChip(
                        status = project.status,
                        onClick = { onIsEditingChange(false) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                    )
                    ProjectStatus.entries
                        .filter { it != project.status }
                        .forEach { status ->
                            StatusChip(
                                modifier = Modifier.padding(top = 4.dp),
                                status = status,
                                onClick = {
                                    onIsEditingChange(false)
                                    onStatusUpdate(status)
                                }
                            )
                        }
                }
            }
        } else {
            StatusChip(
                status = project.status,
                onClick = { onIsEditingChange(true) }
            )
        }
    }
}

@Composable
private fun StatusChip(
    status: ProjectStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .background(
                color = getStatusColor(status),
                shape = RoundedCornerShape(percent = 50),
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clickable { onClick() },
        text = stringResource(getStatusLabelRes(status))
    )
}

@Composable
private fun ProjectDescriptionSection(project: Project) {
    Column {
        Text(stringResource(R.string.new_project_description))
        if (!project.description.isNullOrBlank()) {
            Text(project.description)
        } else {
            Button(
                onClick = { /* TODO */ }
            ) {
                Text(stringResource(R.string.add_label))
            }
        }
    }
}

@Composable
private fun measureTextHeight(text: String, style: TextStyle): Dp {
    val textMeasurer = rememberTextMeasurer()
    val heightPixels = textMeasurer.measure(text, style).size.height
    return with(LocalDensity.current) { heightPixels.toDp() }
}

enum class EditingField {
    NONE,
    TITLE,
    STATUS,
    DESCRIPTION,
    GOAL;
}
