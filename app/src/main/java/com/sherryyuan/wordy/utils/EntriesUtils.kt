package com.sherryyuan.wordy.utils

import com.sherryyuan.wordy.entitymodels.DailyEntry
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

fun List<DailyEntry>.fromPastDays(days: Int): List<DailyEntry> {
    return this.filter { entry ->
        val midnight = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val pastDaysMillis = TimeUnit.DAYS.toMillis(days.toLong() - 1)

        entry.timestamp >= (midnight - pastDaysMillis)
    }
}
