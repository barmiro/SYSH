package com.github.barmiro.syshclient.presentation.top.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.TopScreenState
import com.github.barmiro.syshclient.util.monthToDateRange
import com.github.barmiro.syshclient.util.monthToEnd
import com.github.barmiro.syshclient.util.monthToStart
import com.github.barmiro.syshclient.util.yearToDateRange
import com.github.barmiro.syshclient.util.yearToEnd
import com.github.barmiro.syshclient.util.yearToStart
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopScreenTopBar(
    state: TopScreenState,
    dateRangeMode: String,
    onDateRangeSelect: (Pair<Long?, Long?>?) -> Unit,
    onDateRangeModeChange: (String) -> Unit,
    onDateRangePickerVisibilityChange: (Boolean) -> Unit,
    onVMSearchParameterChange: (TopScreenEvent.OnSearchParameterChange) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            Text(text = "Top",
                fontWeight = FontWeight.Bold)
        },
        navigationIcon = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.DateRange, contentDescription = "Date range options")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All time") },
                    onClick = {
                        onDateRangeSelect(null)
                        onDateRangeModeChange("")
                        onVMSearchParameterChange(
                            TopScreenEvent.OnSearchParameterChange(
                                start = "",
                                end = ""
                            )
                        )
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Yearly") },
                    onClick = {
                        if (dateRangeMode != "yearly") {
                            val year: Int = LocalDate.now().year
                            onDateRangeSelect(yearToDateRange(year))
                            onDateRangeModeChange("yearly")
                            onVMSearchParameterChange(
                                TopScreenEvent.OnSearchParameterChange(
                                    start = yearToStart(year),
                                    end = yearToEnd(year)
                                )
                            )
                        }

                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Monthly") },
                    onClick = {
                        if (dateRangeMode != "monthly") {
                            val month: YearMonth = YearMonth.now()
                            onDateRangeSelect(monthToDateRange(month))
                            onDateRangeModeChange("monthly")
                            onVMSearchParameterChange(
                                TopScreenEvent.OnSearchParameterChange(
                                    start = monthToStart(month) , end = monthToEnd(month)
                                )
                            )
                        }

                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Custom range") },
                    onClick = {
                        onDateRangePickerVisibilityChange(true)
                        expanded = false
                    }
                )
            }
        },
        actions = {
            if (state.sort == "time") {
                IconButton(
                    onClick = {
                        onVMSearchParameterChange(
                            TopScreenEvent.OnSearchParameterChange("count")
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sort_24px),
                        tint = IconButtonDefaults.iconButtonColors().contentColor,
                        contentDescription = "Sort icon"
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        onVMSearchParameterChange(
                            TopScreenEvent.OnSearchParameterChange("time")
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sort_24px),
                        tint = IconButtonDefaults.iconButtonColors().contentColor,
                        contentDescription = "Sort icon"
                    )
                }
            }
        }
    )
}