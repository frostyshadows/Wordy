package com.sherryyuan.wordy.screens.projectslist

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.ui.VerticalSpacer
import com.sherryyuan.wordy.ui.topAndSideContentPadding
import com.sherryyuan.wordy.utils.TOP_BAR_ANIMATION_KEY
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ProjectsListScreen(
    onNavigateToAddProject: () -> Unit,
    onNavigateToProjectDetail: (id: Long) -> Unit,
    topBarAnimatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ProjectsListViewModel = hiltViewModel<ProjectsListViewModel>()
) {
    val viewState by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            ProjectListTopAppBar(
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = TOP_BAR_ANIMATION_KEY),
                        animatedVisibilityScope = topBarAnimatedVisibilityScope,
                    )
                    .skipToLookaheadSize(),
                onAddProjectClick = onNavigateToAddProject,
            )
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .topAndSideContentPadding(contentPadding),
            contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())
        ) {
            viewState.sections.forEach { section ->
                if (section.projectsWithWordCount.isEmpty()) return@forEach
                stickyHeader {
                    Text(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.background)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        text = stringResource(section.titleRes),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                items(
                    items = section.projectsWithWordCount,
                    key = { it.first.id },
                ) {
                    ProjectCard(
                        projectWithWordCount = it,
                        onClick = { onNavigateToProjectDetail(it.first.id) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectListTopAppBar(
    modifier: Modifier = Modifier,
    onAddProjectClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.projects_label),
            )
        },
        actions = {
            IconButton(
                onClick = { onAddProjectClick() }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.new_project_button),
                )
            }
        },
    )
}

@Composable
private fun ProjectCard(
    projectWithWordCount: Pair<Project, Int>,
    onClick: () -> Unit,
) {
    val dateTimeFormatter = DateTimeFormatter
        .ofPattern("MMM d, yyyy")
        .withZone(ZoneId.systemDefault())
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onClick() },
    ) {
        val (project, wordCount) = projectWithWordCount
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                VerticalSpacer(4)
                when (val goal = project.goal) {
                    is Goal.DailyWordCountGoal -> {
                        Text(
                            text = stringResource(R.string.daily_goal_progress_template, wordCount),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        Text(
                            stringResource(
                                R.string.daily_goal_goal_template,
                                goal.initialDailyWordCount
                            )
                        )
                    }

                    is Goal.DeadlineGoal -> {
                        val percentComplete = wordCount * 100 / goal.targetTotalWordCount
                        Text(
                            stringResource(
                                R.string.deadline_goal_progress_template,
                                percentComplete,
                                wordCount,
                                goal.targetTotalWordCount,
                            )
                        )
                        Text(
                            stringResource(
                                R.string.deadline_goal_goal_template,
                                goal.targetTotalWordCount,
                                goal.targetEndDate.format(dateTimeFormatter),
                            )
                        )
                    }
                }
            }
        }
    }
}
