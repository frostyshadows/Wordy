package com.sherryyuan.wordy.utils

import java.util.Date
import java.util.concurrent.TimeUnit

fun getDaysBetween(startDateTimestamp: Long, endDateTimestamp: Long): Long {
    val diffInMillis = endDateTimestamp - startDateTimestamp
    return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
}
