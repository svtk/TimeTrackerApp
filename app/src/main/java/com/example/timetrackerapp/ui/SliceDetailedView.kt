package com.example.timetrackerapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timetrackerapp.model.*
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme
import com.example.timetrackerapp.util.*
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun SliceDetailedView(
    slice: WorkSlice,
    duration: Duration,
    onProjectChanged: (String) -> Unit,
    onTaskChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onFinishClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onStartTimeChange: (LocalDateTime) -> Unit,
    onFinishDateChange: (LocalDate) -> Unit,
    onFinishTimeChange: (LocalDateTime) -> Unit,
    onDurationChange: (Duration) -> Unit
) {
    Column(Modifier.padding(8.dp)) {
        SliceItem(
            "Description", slice.description.value, onDescriptionChanged
        )
        SliceItem(
            "Project", slice.project.value, onProjectChanged
        )
        SliceItem(
            "Task", slice.task.value, onTaskChanged
        )
        DateTimeFields(
            slice.startTime, "Start date", "Start time",
            onStartDateChange, onStartTimeChange,
        )
        if (slice.state == WorkSlice.State.FINISHED) {
            SliceItem(
                "Duration",
                duration.renderDurationFinished(),
                onValueChange = { value -> Duration.parseOrNull(value)?.let(onDurationChange) }
            )
        } else {
            Text(
                text = duration.renderDurationLive(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp),
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
            )
        }
        if (slice.state == WorkSlice.State.FINISHED) {
            DateTimeFields(
                slice.finishTime, "End date", "End time",
                onFinishDateChange, onFinishTimeChange,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (slice.state) {
                WorkSlice.State.PAUSED -> {
                    TwoButtons(
                        leftTitle = "RESUME",
                        rightTitle = "FINISH",
                        onLeftClicked = onResumeClicked,
                        onRightClicked = onFinishClicked
                    )
                }
                WorkSlice.State.RUNNING -> {
                    TwoButtons(
                        leftTitle = "PAUSE",
                        rightTitle = "FINISH",
                        onLeftClicked = onPauseClicked,
                        onRightClicked = onFinishClicked
                    )
                }
                WorkSlice.State.FINISHED -> {}
            }
        }
        OutlinedButton(
            onClick = onBackClicked,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("BACK")
        }
    }
}

@Composable
private fun SliceItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) }
    )
}

@Composable
private fun DateTimeFields(
    time: LocalDateTime,
    dateLabel: String,
    timeLabel: String,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalDateTime) -> Unit,
) {
    val dateDialogState = rememberMaterialDialogState()
    DatePicker(
        dateDialogState = dateDialogState,
        onDateChange = onDateChange
    )
    val timeDialogState = rememberMaterialDialogState()
    TimePicker(
        timeDialogState = timeDialogState,
        date = time.date,
        onTimeChange = onTimeChange
    )

    Row {
        OutlinedTextField(
            value = time.renderDate(),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(end = 4.dp)
                .clickable(onClick = { dateDialogState.show() })
            ,
            readOnly = true,
            enabled = false,
            label = { Text(dateLabel) }
        )
        OutlinedTextField(
            value = time.renderTime(),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)
                .clickable(onClick = { timeDialogState.show() })
            ,
            readOnly = true,
            enabled = false,
            label = { Text(timeLabel) }
        )
    }
}

@Composable
private fun TwoButtons(
    leftTitle: String,
    rightTitle: String,
    onLeftClicked: () -> Unit,
    onRightClicked: () -> Unit
) {
    val textModifier = Modifier
        .padding(top = 16.dp, bottom = 16.dp)
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .padding(end = 4.dp),
        onClick = onLeftClicked,
    ) {
        Text(
            text = leftTitle,
            modifier = textModifier
        )
    }
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp),
        onClick = onRightClicked
    ) {
        Text(
            text = rightTitle,
            modifier = textModifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FinishedSliceDetailedViewPreview() {
    TimeTrackerAppTheme {
        SliceDetailedView(
            slice = FinishedSlice(
                id = 0,
                project = Project("my project"),
                task = Task("my task"),
                description = Description("my work"),
                startInstant = testInstant("2022-01-26T11:30"),
                finishInstant = testInstant("2022-01-26T13:00"),
                duration = 1.hours,
            ),
            duration = 50.minutes,
            onProjectChanged = {},
            onTaskChanged = {},
            onDescriptionChanged = {},
            onPauseClicked = {},
            onResumeClicked = {},
            onFinishClicked = {},
            onBackClicked = {},
            onStartDateChange = {},
            onStartTimeChange = {},
            onFinishDateChange = {},
            onFinishTimeChange = {},
            onDurationChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RunningSliceDetailedViewPreview() {
    TimeTrackerAppTheme {
        SliceDetailedView(
            slice = RunningSlice(
                id = 0,
                project = Project("my project"),
                task = Task("my task"),
                description = Description("my work"),
                state = WorkSlice.State.PAUSED,
                intervals = testTimeIntervals()
            ),
            duration = 50.minutes,
            onProjectChanged = {},
            onTaskChanged = {},
            onDescriptionChanged = {},
            onPauseClicked = {},
            onResumeClicked = {},
            onFinishClicked = {},
            onBackClicked = {},
            onStartDateChange = {},
            onStartTimeChange = {},
            onFinishDateChange = {},
            onFinishTimeChange = {},
            onDurationChange = {},
        )
    }
}