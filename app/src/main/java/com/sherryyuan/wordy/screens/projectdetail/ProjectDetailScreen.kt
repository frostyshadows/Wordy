package com.sherryyuan.wordy.screens.projectdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.screens.projectslist.getStatusColor
import com.sherryyuan.wordy.screens.projectslist.getStatusLabel

@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel = hiltViewModel<ProjectDetailViewModel>()
) {
    val viewState by viewModel.state.collectAsState()
    when (val state = viewState) {
        is ProjectDetailViewState.Loaded -> LoadedProjectDetails(state.projectWithWordCount.first)
        ProjectDetailViewState.Loading -> {}
    }
}

@Composable
fun LoadedProjectDetails(project: Project) {
    Column {
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(project.title)
                project.description?.let {
                    Text(project.description)
                }
            }
            Text(
                modifier = Modifier.background(
                    color = getStatusColor(project.status),
                    shape = RoundedCornerShape(percent = 50),
                ),
                text = stringResource(getStatusLabel(project.status))
            )
        }
    }
}
