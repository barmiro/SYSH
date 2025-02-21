package com.github.barmiro.syshclient.util

import java.util.Calendar
import java.util.Date

fun setToEndOfDay(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)

    return calendar.time
}