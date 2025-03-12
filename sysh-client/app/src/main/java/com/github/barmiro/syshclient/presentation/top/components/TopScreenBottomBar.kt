package com.github.barmiro.syshclient.presentation.top.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.TopScreenState
import com.github.barmiro.syshclient.util.monthToEnd
import com.github.barmiro.syshclient.util.monthToStart
import com.github.barmiro.syshclient.util.yearToEnd
import com.github.barmiro.syshclient.util.yearToStart
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun TopScreenBottomBar(
    state: TopScreenState,
//    dateRange: Pair<Long?, Long?>?,
//    onDateRangeChange: (Pair<Long?, Long?>?) -> Unit,
    onVMSearchParameterChange: (TopScreenEvent.OnSearchParameterChange) -> Unit
    ) {

    var pageCount by remember { mutableIntStateOf(100) }
    var pagerState = rememberPagerState(pageCount = { pageCount }, initialPage = pageCount - 1)
    var targetPage by remember { mutableIntStateOf(pagerState.currentPage) }
    var pageTextGenerator by remember { mutableStateOf<((Int) -> String)>({ "Page $it" }) }



    LaunchedEffect(targetPage) {
            pagerState.animateScrollToPage(targetPage)

    }

    LaunchedEffect(state.dateRangeMode) {
        pageCount = when (state.dateRangeMode) {
            "yearly" -> LocalDate.now().year - state.oldestStreamDate!!.year + 1
            "monthly" -> YearMonth.from(state.oldestStreamDate)
                .until(YearMonth.from(LocalDate.now()), ChronoUnit.MONTHS)
                .toInt() + 1
            else -> 1
        }
        targetPage = pageCount - 1
    }

    LaunchedEffect(targetPage, state.dateRangeMode) {
//        to start loading data before the page settles

            if (state.dateRangeMode == "yearly") {
                val year = pageNumberToYear(
                    page = targetPage,
                    pageCount = pageCount
                )

                pageTextGenerator = { page ->
                    pageNumberToYear(
                        page = page,
                        pageCount = pageCount)
                        .toString()
                }

//                onDateRangeChange(yearToDateRange(year))
                onVMSearchParameterChange(
                    TopScreenEvent.OnSearchParameterChange(
                        start = yearToStart(year),
                        end = yearToEnd(year)))
            }

            if (state.dateRangeMode == "monthly") {

                val month = pageNumberToMonth(
                    page = targetPage,
                    pageCount = pageCount
                )

                pageTextGenerator = { page ->
                    val monthYear = pageNumberToMonth(
                        page = page,
                        pageCount = pageCount)
                    val monthName = monthYear.month.name
                        .lowercase()
                        .replaceFirstChar { it.uppercaseChar() }
                    val year = monthYear.year
                    "$monthName $year"
                }

//                onDateRangeChange(monthToDateRange(month))
                onVMSearchParameterChange(
                    TopScreenEvent.OnSearchParameterChange(
                        start = monthToStart(month),
                        end = monthToEnd(month)
                    )
                )
            }

    }



    if (!state.dateRangeMode.isNullOrEmpty()) {
        Box (modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Surface(modifier = Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colorScheme.background)) {
                    HorizontalDivider(thickness = 2.dp, modifier = Modifier.alpha(0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.dateRangeMode == "custom") {
                            BottomBarNewCustomText(state.start, state.end)
                        } else {
                            Column(modifier = Modifier.fillMaxWidth().weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (state.dateRangeMode == "monthly") {
                                    LeftBottomBarArrow(
                                        currentPage = targetPage,
                                        onTargetPageChange = { targetPage = it},
                                        jump = 12
                                    )
                                }
                            }

                            Column(modifier = Modifier.fillMaxWidth().weight(2f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                LeftBottomBarArrow(
                                    currentPage = targetPage,
                                    onTargetPageChange = { targetPage = it}
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth().weight(4f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxWidth()
                                ) { page ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(pageTextGenerator(page))

                                    }
                                }
                            }

                            Column(modifier = Modifier.fillMaxWidth().weight(2f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                RightBottomBarArrow(
                                    currentPage = targetPage,
                                    onTargetPageChange = { targetPage = it },
                                    pageCount = pageCount
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth().weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (state.dateRangeMode == "monthly") {
                                    RightBottomBarArrow(
                                        currentPage = targetPage,
                                        onTargetPageChange = { targetPage = it},
                                        pageCount = pageCount,
                                        jump = 12
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
//TODO: i in 0 UNTIL

fun pageNumberToYear(page: Int, pageCount: Int): Int {
    return LocalDate.now().year - (pageCount - page - 1)
}

fun pageNumberToMonth(page: Int, pageCount: Int): YearMonth {
    return YearMonth
        .now()
        .minusMonths(pageCount - page - 1L)

}

@Composable
fun BottomBarNewCustomText(start: String?, end:String?) {
    if (start != null && end != null) {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val startDateTime = LocalDateTime.parse(start, inputFormatter)
        val endDateTime = LocalDateTime.parse(end, inputFormatter)

        val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        val formattedStart = startDateTime.format(outputFormatter)
        val formattedEnd = endDateTime.format(outputFormatter)

        Text("$formattedStart - $formattedEnd")
    }
}

@Composable
fun BottomBarCustomRangeText(dateRange: Pair<Long?, Long?>?) {
    if (dateRange != null) {
        val formattedStart = SimpleDateFormat(
            "MMM dd, yyyy",
            Locale.getDefault()
        ).format(dateRange.first)

        val formattedEnd = SimpleDateFormat(
            "MMM dd, yyyy",
            Locale.getDefault()
        ).format(dateRange.second)

        Text("$formattedStart - $formattedEnd")
    }
}

@Composable
fun LeftBottomBarArrow(currentPage: Int,
                       onTargetPageChange: (Int) -> Unit,
                       jump: Int = 1
) {
    IconButton(
        onClick = {
            var adjustedJump = jump
            if (currentPage < jump) {
                adjustedJump = currentPage
            }
            onTargetPageChange(currentPage - adjustedJump)
        },
        enabled = when {
            currentPage == 0 -> false
            else -> true
        }
    ) {
        if (jump > 1) {
            Icon(
                painter = painterResource(id = R.drawable.keyboard_double_arrow_left_24dp),
                tint = IconButtonDefaults.iconButtonColors().contentColor,
                contentDescription = "Sort icon"
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                tint = IconButtonDefaults.iconButtonColors().contentColor,
                contentDescription = "Left arrow"
            )
        }
    }
}

@Composable
fun RightBottomBarArrow(currentPage: Int,
                        onTargetPageChange: (Int) -> Unit,
                        pageCount: Int,
                        jump: Int = 1
) {

    val lastPage = pageCount - 1
    IconButton(
        onClick = {
            var adjustedJump = jump
            if (currentPage + jump > lastPage) {
                adjustedJump = lastPage - currentPage
            }
            onTargetPageChange(currentPage + adjustedJump)
        },
        enabled = when {
            currentPage == lastPage -> false
            else -> true
        }
    ) {
        if (jump > 1) {
            Icon(
                painter = painterResource(id = R.drawable.keyboard_double_arrow_right_24dp),
                tint = IconButtonDefaults.iconButtonColors().contentColor,
                contentDescription = "Sort icon"
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                tint = IconButtonDefaults.iconButtonColors().contentColor,
                contentDescription = "Left arrow"
            )
        }
    }
}