package com.example.timetrackerapp.ui

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

data class SliceInfoUpdates(
    val onProjectChanged: (String) -> Unit,
    val onTaskChanged: (String) -> Unit,
    val onDescriptionChanged: (String) -> Unit,
    val onStartDateChange: (LocalDate) -> Unit,
    val onStartTimeChange: (LocalDateTime) -> Unit,
    val onFinishDateChange: (LocalDate) -> Unit,
    val onFinishTimeChange: (LocalDateTime) -> Unit,
    val onDurationChange: (Duration) -> Unit,
    val onSave: () -> Unit,
)

data class RunningSliceUpdates(
    val onPauseClicked: () -> Unit,
    val onResumeClicked: () -> Unit,
    val onFinishClicked: () -> Unit,
)

val emptySliceInfoUpdates = SliceInfoUpdates(
    onProjectChanged = {},
    onTaskChanged = {},
    onDescriptionChanged = {},
    onStartDateChange = {},
    onStartTimeChange = {},
    onFinishDateChange = {},
    onFinishTimeChange = {},
    onDurationChange = {},
    onSave = {},
)

val emptyRunningSliceUpdates = RunningSliceUpdates(
    onPauseClicked = {},
    onResumeClicked = {},
    onFinishClicked = {},
)