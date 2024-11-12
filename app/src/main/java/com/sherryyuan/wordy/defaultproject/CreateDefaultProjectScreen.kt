package com.sherryyuan.wordy.defaultproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.sherryyuan.wordy.navigation.NavDestination
import com.sherryyuan.wordy.navigation.previewNavController
import com.sherryyuan.wordy.ui.theme.WordyTheme

@Composable
fun CreateDefaultProjectScreen(
    navController: NavHostController,
    viewModel: CreateDefaultProjectViewModel = hiltViewModel<CreateDefaultProjectViewModel>(),
) {
    val viewState by viewModel.state.collectAsState()

    LaunchedEffect(viewState.state) {
        if (viewState.state == CreateDefaultProjectViewState.State.SUBMITTED) {
            navController.navigate(NavDestination.Home) // TODO handle back navigation
        }
    }

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .padding(24.dp),
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
                onClick = { viewModel.saveDefaultProject() }
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