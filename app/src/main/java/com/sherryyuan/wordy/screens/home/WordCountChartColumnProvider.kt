package com.sherryyuan.wordy.screens.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

/**
 * @param isCumulativeGoal whether the word count goal is cumulative over time.
 */
@Composable
fun rememberWordCountColumnProvider(
    wordCountGoal: Int,
    isCumulativeGoal: Boolean = false,
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
                    shape = CorneredShape(topLeft = CorneredShape.Corner.Rounded, topRight = CorneredShape.Corner.Rounded)
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
