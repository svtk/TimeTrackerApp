package com.example.timetrackerapp.ui

import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDate

@Composable
fun DatePicker(
    dateDialogState: MaterialDialogState,
    onDateChange: (LocalDate) -> Unit
) {
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("Save")
            negativeButton("Cancel")
        }
    ) {
        datepicker { date ->
            onDateChange(date.toKotlinLocalDate())
        }
    }
}

@Composable
fun TimePicker(
    timeDialogState: MaterialDialogState,
    date: LocalDate,
    onTimeChange: (LocalDateTime) -> Unit
) {
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("Save")
            negativeButton("Cancel")
        }
    ) {
        timepicker { time ->
            onTimeChange(
                LocalDateTime(
                    year = date.year,
                    month = date.month,
                    dayOfMonth = date.dayOfMonth,
                    hour = time.hour,
                    minute = time.minute,
                    second = time.second,
                    nanosecond = time.nano
                )
            )
        }
    }
}
