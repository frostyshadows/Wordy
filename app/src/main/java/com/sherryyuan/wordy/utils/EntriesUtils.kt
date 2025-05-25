package com.sherryyuan.wordy.utils

import com.sherryyuan.wordy.entitymodels.DailyEntry
import java.time.LocalDate

fun List<DailyEntry>.fromPastDays(days: Int): List<DailyEntry> {
    return filter { entry ->
        entry.date >= LocalDate.now().minusDays(days.toLong())
    }
}
