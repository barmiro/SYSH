package com.github.barmiro.syshclient.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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

fun localDateFromTimestampString(string: String?): LocalDate? {
    if (string.isNullOrEmpty()) {
        return null
    }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    return try {
        LocalDateTime.parse(string, formatter).toLocalDate()
    } catch (e: DateTimeParseException) {
        null
    }
}