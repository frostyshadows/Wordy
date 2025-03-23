package com.sherryyuan.wordy.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun getDaysBetween(startDateTimestamp: Long, endDateTimestamp: Long): Long {
    val diffInMillis = endDateTimestamp - startDateTimestamp
    return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
}

fun Long.toFormattedTimeString(pattern: String): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}
