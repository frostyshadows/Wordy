package com.sherryyuan.wordy.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sherryyuan.wordy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSwitcherTopAppBar(
    onProjectSwitcherClick: () -> Unit,
    viewModel: ProjectSwitcherTopBarViewModel = hiltViewModel<ProjectSwitcherTopBarViewModel>(),
) {

    val selectedProject by viewModel.selectedProject.collectAsState()

    TopAppBar(
        title = {
            selectedProject?.title?.let {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp),
                    textAlign = TextAlign.Center,
                    text = it,
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onProjectSwitcherClick() }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.switch_icon),
                    contentDescription = stringResource(R.string.switch_label),
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsListTopAppBar(
    onAddProjectClick: () -> Unit,
) {
    TopAppBar(
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
        }
    )
}

enum class TopAppBarStyle {
    NONE,
    PROJECT_SWITCHER,
    PROJECTS_LIST,
}
