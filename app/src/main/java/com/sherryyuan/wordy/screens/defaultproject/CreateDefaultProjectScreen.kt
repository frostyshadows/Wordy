package com.sherryyuan.wordy.screens.defaultproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.navigation.WordyNavDestination
import com.sherryyuan.wordy.navigation.previewNavController
import com.sherryyuan.wordy.ui.theme.WordyTheme
import com.sherryyuan.wordy.ui.topAndSideContentPadding

@Composable
fun CreateDefaultProjectScreen(
    navController: NavHostController,
    isOnboarding: Boolean = false,
    viewModel: CreateDefaultProjectViewModel = hiltViewModel<CreateDefaultProjectViewModel>(),
) {
    val viewState by viewModel.state.collectAsState()
    val defaultProjectTitle = stringResource(R.string.default_just_write_project_title)

    LaunchedEffect(viewState.state) {
        if (viewState.state == CreateDefaultProjectViewState.State.SUBMITTED) {
            navController.navigate(WordyNavDestination.Home) {
                popUpTo(if (isOnboarding) WordyNavDestination.Welcome else WordyNavDestination.CreateDefaultProject()) {
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
            Text(stringResource(R.string.want_to_write_header))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    value = viewState.wordCount,
                    onValueChange = {
                        viewModel.setWordCount(it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Text(stringResource(R.string.words_per_day))
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = viewState.state != CreateDefaultProjectViewState.State.SUBMITTING &&
                        viewState.wordCount.isNotBlank(),
                onClick = { viewModel.saveDefaultProject(defaultProjectTitle) }
            ) {
                Text(stringResource(R.string.confirm_label))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateDefaultProjectPreview() {
    WordyTheme {
        CreateDefaultProjectScreen(navController = previewNavController())
    }
}
