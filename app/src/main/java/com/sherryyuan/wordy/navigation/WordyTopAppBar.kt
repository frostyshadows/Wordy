package com.sherryyuan.wordy.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordyTopAppBar(
    onProjectSwitcherClick: () -> Unit,
    viewModel: WordyTopAppBarViewModel = hiltViewModel<WordyTopAppBarViewModel>(),
) {

    val selectedProject by viewModel.selectedProject.collectAsState()

    TopAppBar(
        title = {},
        actions = {
            IconButton(
                onClick = { onProjectSwitcherClick() }
            ) {
                Text(selectedProject?.title?.substring(0, 1)?.uppercase().orEmpty())
            }
        }
    )
}
