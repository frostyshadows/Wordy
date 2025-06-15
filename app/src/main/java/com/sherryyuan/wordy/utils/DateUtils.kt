package com.sherryyuan.wordy.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

// start and end dates are inclusive
fun projectDaysCount(
    startDate: LocalDate,
    endDate: LocalDate,
): Int = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1

fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

fun LocalDate.toEpochMillis(): Long = atStartOfDay(ZoneId.systemDefault())
    .toInstant()
    .toEpochMilli()

fun generatePastLocalDates(days: Int): List<LocalDate> {
    val today = LocalDate.now()
    val earliestDay = days.toLong() - 1
    return (earliestDay downTo  0).map { today.minusDays(it) }
}
