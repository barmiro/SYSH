package com.github.barmiro.syshclient.util

import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit

fun setToEndOfDay(date: LocalDateTime): LocalDateTime {
    return date
        .truncatedTo(ChronoUnit.DAYS)
        .plusDays(1)
        .minusSeconds(1)
}

fun monthToStart(yearMonth: YearMonth): LocalDateTime {
    return yearMonth
        .atDay(1)
        .atStartOfDay()
}

fun monthToEnd(yearMonth: YearMonth): LocalDateTime {
//    For consistency and in case I change to minusNanos later
    return yearMonth
        .plusMonths(1)
        .atDay(1)
        .atStartOfDay()
        .minusSeconds(1)
}



fun yearToStart(year: Int): LocalDateTime {
    return LocalDateTime
        .of(year, 1, 1, 0, 0)

}

fun yearToEnd(year: Int): LocalDateTime {
    return LocalDateTime
        .of(year + 1, 1, 1, 0, 0)
        .minusSeconds(1)
}
