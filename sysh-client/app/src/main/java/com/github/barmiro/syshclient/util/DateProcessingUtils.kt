package com.github.barmiro.syshclient.util

import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date

fun setToEndOfDay(date: Long): Date {
    val calendar = Calendar.getInstance()
    calendar.time = Date(date)

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)

    return calendar.time
}

fun yearToDateRange(year: Int): Pair<Long, Long> {
    return Pair(
        LocalDateTime
            .of(year, 1, 1, 0, 0, 0)
            .atOffset(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli(),

//        this will be automatically converted to 23:59:59 anyway, set all 0's for consistency
        LocalDateTime
            .of(year, 12, 31, 0, 0, 0)
            .atOffset(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )
}

fun monthToDateRange(yearMonth: YearMonth): Pair<Long, Long> {
    return Pair(
        LocalDateTime
            .of(yearMonth.year, yearMonth.monthValue, 1, 0, 0, 0)
            .atOffset(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli(),

//        this will be automatically converted to 23:59:59 anyway, set all 0's for consistency
        LocalDateTime
            .of(yearMonth.year, yearMonth.monthValue, yearMonth.lengthOfMonth(), 0, 0, 0)
            .atOffset(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )
}

fun monthToStart(yearMonth: YearMonth): String {
    val year = yearMonth.year
    val month = when {
        yearMonth.monthValue < 10 -> "0${yearMonth.monthValue}"
        else -> yearMonth.monthValue.toString()
    }

    return "$year-$month-01T00:00:00"
}


fun monthToEnd(yearMonth: YearMonth): String {
    val year = yearMonth.year
    val month = when {
        yearMonth.monthValue < 10 -> "0${yearMonth.monthValue}"
        else -> yearMonth.monthValue.toString()
    }
    val day = yearMonth.lengthOfMonth()
    return "$year-$month-${day}T23:59:59"
}



fun yearToStart(year: Int): String {
    return "$year-01-01T00:00:00"

}

fun yearToEnd(year: Int): String {
    return "$year-12-31T23:59:59"
}