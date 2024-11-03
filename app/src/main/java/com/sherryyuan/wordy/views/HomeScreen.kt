package com.sherryyuan.wordy.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sherryyuan.wordy.ui.theme.WordyTheme
import com.sherryyuan.wordy.viewmodels.HomeViewModel
import com.sherryyuan.wordy.viewmodels.HomeViewState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val viewState by viewModel.state.collectAsState()
    Scaffold { contentPadding ->
        when (val state = viewState) {
            is HomeViewState.Loading -> {}
            is HomeViewState.Loaded -> LoadedHomeScreen(
                state,
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .padding(24.dp)
            )
        }
    }
}

@Composable
private fun LoadedHomeScreen(viewState: HomeViewState.Loaded, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(viewState.projectTitle)
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadedHomePreview() {
    WordyTheme {
        LoadedHomeScreen(
            viewState = HomeViewState.Loaded(
                projectTitle = "Viridian",
                selectProjectOptions = emptyList(),
                projectDescription = null,
                wordsToday = 0,
                dailyWordCountGoal = 100,
            )
        )
    }
}
