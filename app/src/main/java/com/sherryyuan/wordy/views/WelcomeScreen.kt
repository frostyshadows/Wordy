package com.sherryyuan.wordy.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.ui.theme.WordyTheme

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.welcome_title)
        )
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.welcome_message)
        )
        Spacer(modifier = Modifier.padding(vertical = 24.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {}
        ) {
            Text(stringResource(R.string.new_project_button))
        }
        Spacer(modifier = Modifier.padding(vertical = 24.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {}
        ) {
            Text(stringResource(R.string.just_write_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WordyTheme {
        WelcomeScreen()
    }
}
