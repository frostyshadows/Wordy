package com.sherryyuan.wordy.screens.newproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sherryyuan.wordy.navigation.WordyNavDestination
import com.sherryyuan.wordy.ui.topAndSideContentPadding

@Composable
fun CreateNewProjectScreen(
    navController: NavHostController,
    isOnboarding: Boolean = false,
    viewModel: CreateNewProjectViewModel = hiltViewModel<CreateNewProjectViewModel>(),
) {

    val viewState by viewModel.state.collectAsState()

    LaunchedEffect(viewState.state) {
        if (viewState.state == CreateNewProjectViewState.State.SUBMITTED) {
            navController.navigate(WordyNavDestination.Home) {
                popUpTo(if (isOnboarding) WordyNavDestination.Welcome else WordyNavDestination.CreateNewProject()) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .topAndSideContentPadding(contentPadding)
                .padding(start = 24.dp, top = 24.dp, end = 24.dp)
                .navigationBarsPadding()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (viewState.state) {
                CreateNewProjectViewState.State.EDITING_INFO -> {
                    ProjectInfoEditor(
                        viewState = viewState,
                        onTitleChange = viewModel::setTitle,
                        onDescriptionChange = viewModel::setDescription,
                        onNextClick = viewModel::continueToEditGoal,
                        onGoalTypeSelected = viewModel::setGoalType,
                    )
                }

                CreateNewProjectViewState.State.EDITING_WORD_COUNT_GOAL,
                CreateNewProjectViewState.State.SUBMITTING_WORD_COUNT_GOAL,
                -> WordCountGoalEditor(
                    viewState = viewState,
                    onWordCountChange = viewModel::updateWordCount,
                    onSubmitClick = viewModel::saveProject,
                )

                CreateNewProjectViewState.State.EDITING_DEADLINE_GOAL,
                CreateNewProjectViewState.State.SUBMITTING_DEADLINE_GOAL,
                -> DeadlineGoalEditor(
                    viewState = viewState,
                    onWordCountChange = viewModel::updateWordCount,
                    onStartDateChange = viewModel::updateStartDate,
                    onEndDateChange = viewModel::updateEndDate,
                    onSubmitClick = viewModel::saveProject,
                )

                CreateNewProjectViewState.State.SUBMITTED -> Unit
            }
        }
    }
}
