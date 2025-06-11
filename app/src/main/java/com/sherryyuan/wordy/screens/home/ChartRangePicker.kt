package com.sherryyuan.wordy.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.screens.home.HomeViewState.DisplayedChartRange.ALL_TIME
import com.sherryyuan.wordy.screens.home.HomeViewState.DisplayedChartRange.MONTH
import com.sherryyuan.wordy.screens.home.HomeViewState.DisplayedChartRange.WEEK

@Composable
fun ChartRangePicker(
    selectedChartRange: HomeViewState.DisplayedChartRange,
    onChartRangeSelected: (HomeViewState.DisplayedChartRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        val buttonModifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 4.dp)
        ChartRangeButton(
            modifier = buttonModifier,
            textId = R.string.chart_range_week,
            isSelected = selectedChartRange == WEEK,
            onClick = { onChartRangeSelected(WEEK) }
        )
        ChartRangeButton(
            modifier = buttonModifier,
            textId = R.string.chart_range_month,
            isSelected = selectedChartRange == MONTH,
            onClick = { onChartRangeSelected(MONTH) }
        )
        ChartRangeButton(
            modifier = buttonModifier,
            textId = R.string.chart_range_all,
            isSelected = selectedChartRange == ALL_TIME,
            onClick = { onChartRangeSelected(ALL_TIME) }
        )
    }
}

@Composable
private fun ChartRangeButton(
    @StringRes textId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isSelected) {
        Button(
            modifier = modifier,
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(stringResource(textId))
        }
    } else {
        TextButton(
            modifier = modifier,
            onClick = onClick,
        ) {
            Text(stringResource(textId))
        }
    }
}
