package com.example.timetrackerapp.ui.util

import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalTime

@Composable
fun DatePicker(
    dateDialogState: MaterialDialogState,
    initialDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("Save")
            negativeButton("Cancel")
        }
    ) {
        datepicker(
            initialDate = initialDate.toJavaLocalDate(),
            onDateChange = { date ->
                onDateChange(date.toKotlinLocalDate())
            }
        )
    }
}

@Composable
fun TimePicker(
    timeDialogState: MaterialDialogState,
    initialTime: LocalDateTime,
    onTimeChange: (LocalDateTime) -> Unit
) {
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("Save")
            negativeButton("Cancel")
        }
    ) {
        timepicker(
            initialTime = LocalTime.of(initialTime.hour, initialTime.minute, initialTime.second),
            onTimeChange = { time ->
                onTimeChange(
                    LocalDateTime(
                        year = initialTime.year,
                        month = initialTime.month,
                        dayOfMonth = initialTime.dayOfMonth,
                        hour = time.hour,
                        minute = time.minute,
                        second = time.second,
                        nanosecond = time.nano
                    )
                )
            }
        )
    }
}
