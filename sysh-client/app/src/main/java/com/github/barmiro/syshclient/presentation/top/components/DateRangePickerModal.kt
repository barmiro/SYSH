package com.github.barmiro.syshclient.presentation.top.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.util.setToEndOfDay
import java.text.SimpleDateFormat
import java.time.LocalDateTime
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
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (dateRangePickerState.selectedStartDateMillis != null) {
                        if (dateRangePickerState.selectedEndDateMillis == null) {
                            dateRangePickerState.setSelection(
                                dateRangePickerState.selectedStartDateMillis,
                                LocalDateTime.now()
                                    .atOffset(ZoneOffset.UTC)
                                    .toInstant()
                                    .toEpochMilli())
                        }

                        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        val start = formatter.format(dateRangePickerState.selectedStartDateMillis)
                        val end = formatter.format(setToEndOfDay(dateRangePickerState.selectedEndDateMillis!!))

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
                    text = "Select date range"
                )
            },
            showModeToggle = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}