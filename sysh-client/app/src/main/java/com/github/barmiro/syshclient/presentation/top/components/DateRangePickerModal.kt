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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.util.setToEndOfDay
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onVMSearchParameterChange: (TopScreenEvent.OnSearchParameterChange) -> Unit,
    onDateRangeModeChange: (TopScreenEvent.OnDateRangeModeChange) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    dateRangePickerState.selectedStartDateMillis?.let { startDateMillis ->

                        val endDateMillis = dateRangePickerState.selectedEndDateMillis
                            ?: LocalDateTime.now()
                            .atOffset(ZoneOffset.UTC)
                            .toInstant()
                            .toEpochMilli()

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

                        onVMSearchParameterChange(TopScreenEvent.OnSearchParameterChange(null, start, end))
                        onDateRangeModeChange(TopScreenEvent.OnDateRangeModeChange("custom"))
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