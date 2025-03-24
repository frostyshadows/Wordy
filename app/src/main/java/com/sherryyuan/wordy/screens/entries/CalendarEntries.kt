package com.sherryyuan.wordy.screens.entries

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.screens.entries.EntriesViewState.CalendarEntriesProgress
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarEntries(
    entriesState: EntriesViewState.CalendarEntries,
    calendarState: CalendarState,
    currentYearMonth: YearMonth,
    daysOfWeek: List<DayOfWeek>,
) {
    val visibleYearMonth = calendarState.firstVisibleMonth.yearMonth
    var selectedDay by remember {
        mutableStateOf<CalendarDay?>(CalendarDay(LocalDate.now(), DayPosition.MonthDate))
    }
    val coroutineScope = rememberCoroutineScope()
    CalendarHeader(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
        currentYearMonth = visibleYearMonth,
        goToPrevious = if (visibleYearMonth.isAfter(entriesState.startYearMonth)) {
            {
                coroutineScope.launch {
                    calendarState.animateScrollToMonth(visibleYearMonth.previousMonth)
                }
            }
        } else null,
        goToNext = if (visibleYearMonth.isBefore(currentYearMonth)) {
            {
                coroutineScope.launch {
                    calendarState.animateScrollToMonth(visibleYearMonth.nextMonth)
                }
            }
        } else null,
    )
    HorizontalCalendar(
        state = calendarState,
        monthHeader = {
            MonthHeader(daysOfWeek = daysOfWeek)
        },
        dayContent = { day ->
            val dayEntries = entriesState.dailyEntries.firstOrNull {
                it.date == day.date
            }
            val isSelected = selectedDay == day
            DayContent(
                calendarDay = day,
                entries = dayEntries,
                isSelected = isSelected,
                onClick = {
                    if (!isSelected) {
                        selectedDay = day
                    } else {
                        selectedDay = null
                    }
                }
            )
        },
    )
}

@Composable
private fun CalendarHeader(
    modifier: Modifier,
    currentYearMonth: YearMonth,
    goToPrevious: (() -> Unit)?,
    goToNext: (() -> Unit)?,
) {
    Row(
        modifier = modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        goToPrevious?.let {
            IconButton(onClick = it) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.previous_label),
                )
            }
        } ?: CalendarHeaderSpacer()
        val monthYearText =
            "${currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} " +
                    "${currentYearMonth.year}"
        Text(
            modifier = Modifier.weight(1f),
            text = monthYearText,
            textAlign = TextAlign.Center,
        )
        goToNext?.let {
            IconButton(onClick = it) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.next_label),
                )
            }
        } ?: CalendarHeaderSpacer()
    }
}

@Composable
private fun CalendarHeaderSpacer() = Box(
    modifier = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
)

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}

@Composable
private fun DayContent(
    calendarDay: CalendarDay,
    entries: EntriesViewState.CalendarEntries.DailyCalendarEntries?,
    isSelected: Boolean,
    onClick: (CalendarDay) -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.tertiary else Color.Transparent,
                shape = CircleShape,
            )
            .clickable(
                enabled = calendarDay.position == DayPosition.MonthDate,
                onClick = { onClick(calendarDay) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (calendarDay.position == DayPosition.MonthDate) {
            when (val progress = entries?.progress) {
                CalendarEntriesProgress.GoalAchieved -> GoalAchievedBackground()
                CalendarEntriesProgress.GoalAchievedStreakStart -> GoalAchievedStreakStartBackground()
                CalendarEntriesProgress.GoalAchievedStreakMiddle -> GoalAchievedStreakMiddleBackground()
                CalendarEntriesProgress.GoalAchievedStreakEnd -> GoalAchievedStreakEndBackground()
                is CalendarEntriesProgress.GoalProgress -> GoalProgressBackground(progress)

                null -> Unit
            }
        }
        val textColor = when (calendarDay.position) {
            DayPosition.MonthDate -> Color.Unspecified
            DayPosition.InDate, DayPosition.OutDate -> Color.LightGray
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = calendarDay.date.dayOfMonth.toString(),
                color = textColor,
            )
            if (calendarDay.date == LocalDate.now()) {
                Text(
                    text = "Today", // TODO revisit styling
                    fontSize = 10.sp,
                    color = textColor,
                )
            }
        }
    }
}

@Composable
private fun GoalAchievedBackground() {
    val radialGradientColors = radialGradientColors()
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        drawCircle(brush = Brush.radialGradient(radialGradientColors))
    }
}

@Composable
private fun GoalAchievedStreakStartBackground() {
    val radialGradientColors = radialGradientColors()
    val verticalGradientColors = verticalGradientColors()
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
    ) {
        val width = size.width
        val height = size.height

        // Left semi-circle
        drawArc(
            brush = Brush.radialGradient(radialGradientColors),
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = true,
            size = Size(width, height)
        )

        // Fully filled right half
        drawRect(
            brush = Brush.verticalGradient(verticalGradientColors),
            topLeft = Offset(width / 2, 0f),
            size = Size(width / 2, height)
        )
    }
}

@Composable
private fun GoalAchievedStreakMiddleBackground() {
    val verticalGradientColors = verticalGradientColors()
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        drawRect(brush = Brush.verticalGradient(colors = verticalGradientColors))
    }
}

@Composable
private fun GoalAchievedStreakEndBackground() {
    val radialGradientColors = radialGradientColors()
    val verticalGradientColors = verticalGradientColors()
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp, bottom = 4.dp, end = 4.dp)
    ) {
        val width = size.width
        val height = size.height

        // Fully filled left half
        drawRect(
            brush = Brush.verticalGradient(colors = verticalGradientColors),
            topLeft = Offset(0f, 0f),
            size = Size(width / 2, height)
        )

        // Right semi-circle
        drawArc(
            brush = Brush.radialGradient(colors = radialGradientColors),
            startAngle = 270f,
            sweepAngle = 180f,
            useCenter = true,
            size = Size(width, height)
        )
    }
}

@Composable
private fun GoalProgressBackground(progress: CalendarEntriesProgress.GoalProgress) {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        progress = { progress.percentAchieved },
    )
}

@ReadOnlyComposable
@Composable
private fun radialGradientColors(): List<Color> {
    val edgeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val centerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    return listOf(centerColor, centerColor, edgeColor)
}

@ReadOnlyComposable
@Composable
private fun verticalGradientColors(): List<Color> {
    val edgeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val centerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    return listOf(edgeColor, centerColor, centerColor, centerColor, edgeColor)
}
