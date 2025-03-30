package com.sherryyuan.wordy.screens.home

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.ui.theme.VerticalSpacer
import com.sherryyuan.wordy.ui.theme.WordyTheme
import com.sherryyuan.wordy.ui.topAndSideContentPadding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    topBar: @Composable () -> Unit,
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val viewState by viewModel.state.collectAsState()
    Scaffold(
        topBar = { topBar() }
    ) { contentPadding ->
        when (val state = viewState) {
            is HomeViewState.Loading -> {}
            is HomeViewState.Loaded -> {
                val keyboardController = LocalSoftwareKeyboardController.current
                LoadedHomeScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .topAndSideContentPadding(contentPadding)
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
}

@Composable
private fun LoadedHomeScreen(
    viewState: HomeViewState.Loaded,
    onWordCountInputChange: (String) -> Unit,
    onWordCountInputSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
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

        if (viewState.chartWordCounts.isNotEmpty()) {
            VerticalSpacer()
            when (viewState.selectedDisplayedChartRange) {
                HomeViewState.DisplayedChartRange.PROJECT_WITH_DEADLINE -> CumulativeWordCountChart(
                    viewState.chartWordCounts,
                    viewState.dailyWordCountGoal,
                )

                else -> DailyWordCountChart(
                    viewState.chartWordCounts,
                    viewState.dailyWordCountGoal,
                )
            }
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

@Composable
private fun DailyWordCountChart(
    wordCounts: Map<Long, Int>,
    wordCountGoal: Int,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val xAxisDates = rememberXAxisDates(wordCounts.keys.toList())
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(wordCounts.keys, wordCounts.values) }
        }
    }

    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    rememberDailyWordCountColumnProvider(wordCountGoal)
                ),
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, x, _ ->
                        xAxisDates[x.toInt()]
                    }
                ),
                decorations = listOf(
                    HorizontalLine(
                        y = { wordCountGoal.toDouble() },
                        line = rememberLineComponent(),
                    )
                ),
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
            scrollState = rememberVicoScrollState(scrollEnabled = false),
        )
    }
}

@Composable
private fun CumulativeWordCountChart(
    wordCounts: Map<Long, Int>,
    wordCountGoal: Int,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val xAxisDates = rememberXAxisDates(wordCounts.keys.toList())
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(wordCounts.values) }
            val targetLine = List(wordCounts.size) { index ->
                (index + 1) * wordCountGoal
            }
            lineSeries { series(targetLine) }
        }
    }

    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    rememberDailyWordCountColumnProvider(wordCountGoal)
                ),
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, x, _ ->
                        xAxisDates[x.toInt()]
                    }
                ),
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
            scrollState = rememberVicoScrollState(scrollEnabled = false),
        )
    }
}

@Composable
private fun rememberXAxisDates(timestamps: List<Long>): List<String> {
    val dateTimeFormatter = DateTimeFormatter
        .ofPattern("MMM d")
        .withZone(ZoneId.systemDefault())
    return remember {
        timestamps.map {
            dateTimeFormatter.format(Instant.ofEpochMilli(it))
        }
    }
}

// from https://github.com/philipplackner/AnimatedCounterCompose
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedCounter(count: Int) {
    var oldCount by remember { mutableIntStateOf(count) }
    SideEffect {
        oldCount = count
    }
    Row {
        val countString = count.toString()
        val oldCountString = oldCount.toString()
        for (i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
            val char = if (oldChar == newChar) {
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
                selectedDisplayedChartRange = HomeViewState.DisplayedChartRange.WEEK,
                chartWordCounts = mapOf(),
            ),
            onWordCountInputChange = {},
            onWordCountInputSubmit = {},
        )
    }
}
