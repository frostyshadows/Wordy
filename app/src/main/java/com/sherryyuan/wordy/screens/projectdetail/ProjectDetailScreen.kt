package com.sherryyuan.wordy.screens.projectdetail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.screens.projectslist.getStatusColor
import com.sherryyuan.wordy.screens.projectslist.getStatusLabel
import com.sherryyuan.wordy.ui.theme.HorizontalSpacer
import com.sherryyuan.wordy.utils.TOP_BAR_ANIMATION_KEY

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ProjectDetailScreen(
    topBarAnimatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ProjectDetailViewModel = hiltViewModel<ProjectDetailViewModel>()
) {
    val viewState by viewModel.state.collectAsState()
    val projectTitle =
        (viewState as? ProjectDetailViewState.Loaded)?.projectWithWordCount?.first?.title
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
            )
        }
    ) { contentPadding ->
        when (val state = viewState) {
            is ProjectDetailViewState.Loaded -> LoadedProjectDetails(
                modifier = Modifier.padding(contentPadding),
                project = state.projectWithWordCount.first,
                onStatusUpdate = { viewModel.onStatusUpdate(it) },
            )

            ProjectDetailViewState.Loading -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectDetailTopAppBar(
    modifier: Modifier = Modifier,
    projectTitle: String,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = projectTitle,
            )
        }
    )
}

@Composable
private fun LoadedProjectDetails(
    project: Project,
    onStatusUpdate: (ProjectStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        ProjectStatusRow(project, onStatusUpdate)
        project.description?.let {
            Text(project.description)
        }
    }
}

@Composable
private fun ProjectStatusRow(
    project: Project,
    onStatusUpdate: (ProjectStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Row(modifier = modifier) {
        Text(stringResource(R.string.project_detail_status_label))
        HorizontalSpacer(4)
        if (expanded) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
            ) {
                StatusChip(
                    status = project.status,
                    onClick = { expanded = !expanded }
                )
                HorizontalDivider(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .padding(top = 4.dp)
                )
                ProjectStatus.entries
                    .filter { it != project.status }
                    .forEach { status ->
                        StatusChip(
                            modifier = Modifier
                                .padding(top = 4.dp),
                            status = status,
                            onClick = { onStatusUpdate(status) }
                        )
                    }
            }
        } else {
            StatusChip(
                status = project.status,
                onClick = { expanded = !expanded }
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
        text = stringResource(getStatusLabel(status))
    )
}
