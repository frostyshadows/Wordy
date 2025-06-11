package com.sherryyuan.wordy.screens.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
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
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val X_AXIS_PLACEHOLDER = "..."

@Composable
fun DailyWordCountChart(
    wordCounts: Map<LocalDate, Int>,
    wordCountGoal: Int,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val xAxisDates = wordCounts.getXAxisDates()
    LaunchedEffect(wordCounts) {
        modelProducer.runTransaction {
            columnSeries { series(wordCounts.values) }
        }
    }

    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    rememberWordCountColumnProvider(wordCountGoal, isCumulativeGoal = false)
                ),
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, x, _ ->
                        xAxisDates.getOrNull(x.toInt()) ?: X_AXIS_PLACEHOLDER
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
fun CumulativeWordCountChart(
    wordCounts: Map<LocalDate, Int>,
    wordCountGoal: Int,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val xAxisDates = wordCounts.getXAxisDates()
    LaunchedEffect(wordCounts) {
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
                    rememberWordCountColumnProvider(wordCountGoal, isCumulativeGoal = true)
                ),
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, x, _ ->
                        xAxisDates.getOrNull(x.toInt()) ?: X_AXIS_PLACEHOLDER
                    }
                ),
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
            scrollState = rememberVicoScrollState(scrollEnabled = false),
        )
    }
}

private fun Map<LocalDate, Int>.getXAxisDates(): List<String> {
    val dateTimeFormatter = DateTimeFormatter
        .ofPattern("MMM d")
        .withZone(ZoneId.systemDefault())
    return keys.toList().map {
        dateTimeFormatter.format(it)
    }
}

/**
 * @param isCumulativeGoal whether the word count goal is cumulative over time.
 */
@Composable
private fun rememberWordCountColumnProvider(
    wordCountGoal: Int,
    isCumulativeGoal: Boolean,
): ColumnCartesianLayer.ColumnProvider {
    val goalMetColor = MaterialTheme.colorScheme.primary
    val defaultColor = MaterialTheme.colorScheme.secondaryContainer
    return remember(wordCountGoal) {
        object : ColumnCartesianLayer.ColumnProvider {
            override fun getColumn(
                entry: ColumnCartesianLayerModel.Entry,
                seriesIndex: Int,
                extraStore: ExtraStore
            ): LineComponent {
                val isDailyGoalMet = !isCumulativeGoal && entry.y >= wordCountGoal
                val isCumulativeGoalMet = isCumulativeGoal && entry.y >= entry.x * wordCountGoal
                val color = if (isDailyGoalMet || isCumulativeGoalMet) {
                    goalMetColor
                } else {
                    defaultColor
                }
                return LineComponent(
                    fill = Fill(color.toArgb()),
                    thicknessDp = 16f,
                    shape = CorneredShape(
                        topLeft = CorneredShape.Corner.Rounded,
                        topRight = CorneredShape.Corner.Rounded
                    )
                )
            }

            override fun getWidestSeriesColumn(
                seriesIndex: Int,
                extraStore: ExtraStore
            ): LineComponent {
                return LineComponent(fill = Fill(defaultColor.toArgb()), thicknessDp = 16f)
            }
        }
    }
}
