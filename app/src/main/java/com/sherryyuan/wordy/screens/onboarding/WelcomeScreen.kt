package com.sherryyuan.wordy.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.ui.VerticalSpacer
import com.sherryyuan.wordy.ui.theme.WordyTheme
import com.sherryyuan.wordy.ui.topAndSideContentPadding

@Composable
fun WelcomeScreen(
    onNavigateToNewProject: () -> Unit,
    onNavigateToDefaultProject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .topAndSideContentPadding(contentPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.welcome_title)
            )
            VerticalSpacer(heightDp = 12)
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.welcome_message)
            )
            VerticalSpacer()
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToNewProject,
            ) {
                Text(stringResource(R.string.new_project_button))
            }
            VerticalSpacer()
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToDefaultProject,
            ) {
                Text(stringResource(R.string.just_write_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomePreview() {
    WordyTheme {
        WelcomeScreen(
            onNavigateToNewProject = {},
            onNavigateToDefaultProject = {},
        )
    }
}
