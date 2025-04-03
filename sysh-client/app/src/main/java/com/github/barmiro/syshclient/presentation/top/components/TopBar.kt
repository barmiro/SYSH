package com.github.barmiro.syshclient.presentation.top.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.barmiro.syshclient.presentation.home.HomeState
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.TopScreenState
import com.github.barmiro.syshclient.util.monthToEnd
import com.github.barmiro.syshclient.util.monthToStart
import com.github.barmiro.syshclient.util.yearToEnd
import com.github.barmiro.syshclient.util.yearToStart
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopScreenTopBar(
    state: TopScreenState,
    onDateRangeModeChange: (String) -> Unit,
    onDateRangePickerVisibilityChange: (Boolean) -> Unit,
    onVMSearchParameterChange: (TopScreenEvent.OnSearchParameterChange) -> Unit,
    onDateRangePageChange: (TopScreenEvent.OnDateRangePageChange) -> Unit,
    titleText: String,
    animatedText: @Composable (RowScope.() -> Unit),
    actions: @Composable (RowScope.() -> Unit)
) {
    var expanded by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = titleText,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())

                }
                Row(
                    content = animatedText
                )
            }
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
                        onDateRangeModeChange("")
                        onDateRangePageChange(TopScreenEvent.OnDateRangePageChange(-1))
                        onVMSearchParameterChange(
                            TopScreenEvent.OnSearchParameterChange(
                                start = LocalDateTime.MIN,
                                end = LocalDateTime.MIN
                            )
                        )
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Yearly") },
                    onClick = {
                        if (state.dateRangeMode != "yearly") {
                            val year: Int = LocalDate.now().year
                            onDateRangeModeChange("yearly")
                            onDateRangePageChange(TopScreenEvent.OnDateRangePageChange(-1))
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
                        if (state.dateRangeMode != "monthly") {
                            val month: YearMonth = YearMonth.now()
                            onDateRangeModeChange("monthly")
                            onDateRangePageChange(TopScreenEvent.OnDateRangePageChange(-1))
                            onVMSearchParameterChange(
                                TopScreenEvent.OnSearchParameterChange(
                                    start = monthToStart(month),
                                    end = monthToEnd(month)
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
        actions = actions
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    state: HomeState,
    titleText: String,
    animatedText: @Composable (RowScope.() -> Unit),
    actions: @Composable (RowScope.() -> Unit)
) {
    var expanded by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = titleText,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())

                }
                Row(
                    content = animatedText
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.DateRange, contentDescription = "Date range options")
            }
        },
        actions = actions
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenTopBar(
    titleText: String,
    actions: @Composable (RowScope.() -> Unit)
) {
    var expanded by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = titleText,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())

                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.DateRange, contentDescription = "Date range options")
            }
        },
        actions = actions
    )
}