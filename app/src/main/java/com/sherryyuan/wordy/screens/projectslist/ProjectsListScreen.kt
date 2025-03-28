package com.sherryyuan.wordy.screens.projectslist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.navigation.WordyNavDestination
import com.sherryyuan.wordy.ui.theme.VerticalSpacer
import com.sherryyuan.wordy.utils.toFormattedTimeString

@Composable
fun ProjectsListScreen(
    navController: NavController,
    viewModel: ProjectsListViewModel = hiltViewModel<ProjectsListViewModel>()
) {
    val viewState by viewModel.state.collectAsState()
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        viewState.sections.forEach { section ->
            if (section.projectsWithWordCount.isEmpty()) return@forEach
            Text(stringResource(section.titleRes))
            section.projectsWithWordCount.forEach {
                ProjectCard(
                    projectWithWordCount = it,
                    onClick = {
                        navController.navigate(WordyNavDestination.ProjectDetail(it.first.id))
                    },
                    onEditButtonClick = {},
                )
            }

        }
    }
}

@Composable
private fun ProjectCard(
    projectWithWordCount: Pair<Project, Int>,
    onClick: () -> Unit,
    onEditButtonClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                        Text(stringResource(R.string.daily_goal_goal_template, goal.dailyWordCount))
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
                        val targetDate =
                            goal.targetEndDateMillis.toFormattedTimeString("MMM d, yyyy")
                        Text(
                            stringResource(
                                R.string.deadline_goal_goal_template,
                                goal.targetTotalWordCount,
                                targetDate,
                            )
                        )
                    }
                }
            }

            IconButton(onClick = onEditButtonClick) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit_label),
                )
            }
        }
    }
}
