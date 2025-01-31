package com.sherryyuan.wordy.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.ui.theme.VerticalSpacer
import com.sherryyuan.wordy.ui.theme.WordyTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val viewState by viewModel.state.collectAsState()
    when (val state = viewState) {
        is HomeViewState.Loading -> {}
        is HomeViewState.Loaded -> {
            val keyboardController = LocalSoftwareKeyboardController.current
            LoadedHomeScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                viewState = state,
                onWordCountInputChange = { viewModel.setWordCount(it) },
                onWordCountInputSubmit = {
                    viewModel.onWordCountInputSubmit()
                    keyboardController?.hide()
                }
            )
        }
    }
}

@Composable
private fun LoadedHomeScreen(
    viewState: HomeViewState.Loaded,
    onWordCountInputChange: (String) -> Unit,
    onWordCountInputSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(viewState.projectTitle)
        VerticalSpacer()

        WordCountInput(viewState, onWordCountInputChange, onWordCountInputSubmit)
        VerticalSpacer(heightDp = 12)

        Row {
            AnimatedCounter(viewState.wordsToday)
            Text(stringResource(R.string.words_today_message))
        }
        VerticalSpacer(heightDp = 4)

        LinearProgressIndicator(
            progress = { viewState.wordsToday.toFloat() / viewState.dailyWordCountGoal },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            drawStopIndicator = {},
        )
        VerticalSpacer(heightDp = 4)
        val remainingWordCount = viewState.dailyWordCountGoal - viewState.wordsToday
        if (remainingWordCount > 0) {
            Text(
                stringResource(
                    R.string.words_to_go_message,
                    remainingWordCount,
                    viewState.dailyWordCountGoal,
                )
            )
        } else {
            Text(stringResource(R.string.goal_achieved))
        }

    }
}

@Composable
private fun WordCountInput(
    viewState: HomeViewState.Loaded,
    onWordCountInputChange: (String) -> Unit,
    onWordCountInputSubmit: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = viewState.currentWordCountInput,
            onValueChange = {
                onWordCountInputChange(it)
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = { onWordCountInputSubmit() },
            enabled = viewState.currentWordCountInput.isNotBlank(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(stringResource(R.string.log_button_label))
        }
    }
}

// from https://github.com/philipplackner/AnimatedCounterCompose
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedCounter(count: Int) {
    var oldCount by remember { mutableIntStateOf(count) }
    SideEffect {
        oldCount = count
    }
    Row {
        val countString = count.toString()
        val oldCountString = oldCount.toString()
        for(i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
            val char = if(oldChar == newChar) {
                oldCountString[i]
            } else {
                countString[i]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically { it } with slideOutVertically { -it }
                },
                label = "animated counter"
            ) { targetChar ->
                Text(text = targetChar.toString(), softWrap = false)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadedHomePreview() {
    WordyTheme {
        LoadedHomeScreen(
            viewState = HomeViewState.Loaded(
                projectTitle = "Viridian",
                projectDescription = null,
                currentWordCountInput = "100",
                wordsToday = 200,
                dailyWordCountGoal = 500,
            ),
            onWordCountInputChange = {},
            onWordCountInputSubmit = {},
        )
    }
}
