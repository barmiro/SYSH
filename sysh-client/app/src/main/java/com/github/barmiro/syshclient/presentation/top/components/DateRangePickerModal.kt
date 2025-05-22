package com.github.barmiro.syshclient.presentation.top.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.util.setToEndOfDay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onVMSearchParameterChange: (TopScreenEvent.OnSearchParameterChange) -> Unit,
    onDateRangeModeChange: (TopScreenEvent.OnDateRangeModeChange) -> Unit,
    onDismiss: () -> Unit,
    oldestStreamDate: LocalDate
) {

    val oldestStreamDateMillis = LocalDateTime
        .of(oldestStreamDate, LocalTime.MIN)
        .atOffset(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    val epochMillisNow = LocalDateTime.now()
        .atOffset(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    val notInTheFuture: SelectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis in oldestStreamDateMillis..<epochMillisNow
        }
    }

    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = notInTheFuture
    )

    DatePickerDialog(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    dateRangePickerState.selectedStartDateMillis?.let { rawStartDateMillis ->



                        val endDateMillis = dateRangePickerState.selectedEndDateMillis
                            ?: epochMillisNow

                        val startDateMillis = min(rawStartDateMillis, endDateMillis)

                        if (dateRangePickerState.selectedEndDateMillis == null) {
                            dateRangePickerState.setSelection(
                                startDateMillis,
                                endDateMillis
                            )
                        }

//                        this is all very bad, but the DateRangePicker is forcing my hand
                        val start = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(startDateMillis),
                            ZoneId.of("UTC")
                        )

                        val end = setToEndOfDay(LocalDateTime
                            .ofInstant(
                            Instant.ofEpochMilli(endDateMillis),
                            ZoneId.of("UTC")
                            )
                        )
//                        onDateRangeModeChange(TopScreenEvent.OnDateRangeModeChange("custom"))
                        onVMSearchParameterChange(
                            TopScreenEvent.OnSearchParameterChange(
                                start = start,
                                end = end,
                                dateRangeMode = "custom",
                                dateRangePage = -1
                            )
                        )
                    }
                    onDismiss()
                },
                enabled = dateRangePickerState.selectedStartDateMillis != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range",
                    modifier = Modifier.padding(16.dp)
                )
            },
//            this is NOT ideal, but the default material 3 config is broken
//            and the text can overflow into multiple lines, this helps a bit
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    dateRangePickerState.selectedStartDateMillis,
                    dateRangePickerState.selectedEndDateMillis,
                    dateRangePickerState.displayMode,
                    DatePickerDefaults.dateFormatter(),
                    Modifier.fillMaxWidth(1f)
                        .padding(horizontal = 16.dp)
                        .offset(y = (-4).dp)
                )
            },
            showModeToggle = true,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

