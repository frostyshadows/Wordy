package com.sherryyuan.wordy.projectswitcher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.navigation.WordyNavDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSwitcherSheet(
    navController: NavHostController,
    onDismiss: () -> Unit,
    viewModel: ProjectSwitcherViewModel = hiltViewModel<ProjectSwitcherViewModel>(),
) {

    val viewState by viewModel.state.collectAsState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        val lineHeight = LocalTextStyle.current.lineHeight.value
        val rowHeight = lineHeight * 2 + 16.sp.value
        LazyColumn {
            items(viewState.options) { option ->
                when (option) {
                    is ProjectSwitcherOption.ProjectWithSelection -> {
                        ProjectWithSelectionRow(
                            option = option,
                            rowHeight = rowHeight,
                            onClick = {
                                if (!option.isSelected) {
                                    viewModel.onProjectSelected(option.project)
                                }
                                onDismiss()
                            }
                        )
                    }

                    ProjectSwitcherOption.NewProject -> NewProjectRow(
                        rowHeight = rowHeight,
                        onClick = {
                            navController.navigate(WordyNavDestination.CreateNewProject)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectWithSelectionRow(
    option: ProjectSwitcherOption.ProjectWithSelection,
    rowHeight: Float,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .height(rowHeight.dp)
            .padding(horizontal = 24.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = option.project.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (option.isSelected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun NewProjectRow(
    rowHeight: Float,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(rowHeight.dp)
            .padding(horizontal = 24.dp),
    ) {
        Text(stringResource(R.string.new_project_button))
    }
}
