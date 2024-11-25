package com.sherryyuan.wordy.utils

import java.util.Date
import java.util.concurrent.TimeUnit

fun getDaysBetween(startDate: Date, endDate: Date): Long {
    val diffInMillis = endDate.time - startDate.time
    return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
}