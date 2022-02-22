package com.example.timetrackerapp.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timetrackerapp.model.*
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme
import com.example.timetrackerapp.util.*
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun SliceDetailedView(
    slice: WorkSlice,
    sliceInfoUpdates: SliceInfoUpdates,
    runningSliceUpdates: RunningSliceUpdates,
    onBackClicked: () -> Unit,
) {
    Card(Modifier.padding(12.dp)) {
        Column(Modifier.padding(12.dp)) {
            SliceItem(
                "Description", slice.description.value, sliceInfoUpdates.onDescriptionChanged,
                colors = TextFieldDefaults.changeBorderColor(),
            )
            SliceItem(
                "Project", slice.project.value, sliceInfoUpdates.onProjectChanged,
            )
            SliceItem(
                "Task", slice.task.value, sliceInfoUpdates.onTaskChanged,
                colors = TextFieldDefaults.changeBorderColor(),
            )
            DateTimeFields(
                slice.startInstant, "Start date", "Start time",
                sliceInfoUpdates.onStartDateChange, sliceInfoUpdates.onStartTimeChange,
            )
            if (slice.state == WorkSlice.State.FINISHED) {
                SliceItem(
                    "Duration",
                    slice.duration.renderDurationFinished(),
                    onValueChange = { value ->
                        Duration.parseOrNull(value)?.let(sliceInfoUpdates.onDurationChange)
                    }
                )
            } else {
                Text(
                    text = slice.duration.renderDurationLive(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp),
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
                )
            }
            if (slice.state == WorkSlice.State.FINISHED) {
                DateTimeFields(
                    slice.finishInstant, "End date", "End time",
                    sliceInfoUpdates.onFinishDateChange, sliceInfoUpdates.onFinishTimeChange,
                )
            }
            if (slice.state != WorkSlice.State.FINISHED) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (slice.state == WorkSlice.State.PAUSED)
                        ActionButton(
                            modifier = Modifier.fillMaxWidth(0.5f),
                            contentDescription = "Resume",
                            imageVector = Icons.Filled.PlayCircle,
                            color = MaterialTheme.colors.secondary,
                            onClick = runningSliceUpdates.onResumeClicked
                        )
                    else
                        ActionButton(
                            modifier = Modifier.fillMaxWidth(0.5f),
                            contentDescription = "Pause",
                            imageVector = Icons.Filled.PauseCircle,
                            color = MaterialTheme.colors.secondary,
                            onClick = runningSliceUpdates.onPauseClicked
                        )
                    ActionButton(
                        modifier = Modifier.fillMaxWidth(),
                        contentDescription = "Finish",
                        imageVector = Icons.Filled.StopCircle,
                        color = MaterialTheme.colors.primary,
                        onClick = runningSliceUpdates.onFinishClicked
                    )
                }
            }
            Row {
                OutlinedButton(
                    onClick = onBackClicked,
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    Text("BACK")
                }
                OutlinedButton(
                    onClick = sliceInfoUpdates.onSave,
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    Text("SAVE")
                }
            }
        }
    }
}

@Composable
private fun SliceItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        colors = colors,
    )
}

@Composable
private fun DateTimeFields(
    instant: Instant,
    dateLabel: String,
    timeLabel: String,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalDateTime) -> Unit,
) {
    val time = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val dateDialogState = rememberMaterialDialogState()
    DatePicker(
        dateDialogState = dateDialogState,
        initialDate = time.date,
        onDateChange = onDateChange
    )
    val timeDialogState = rememberMaterialDialogState()
    TimePicker(
        timeDialogState = timeDialogState,
        initialTime = time,
        onTimeChange = onTimeChange
    )

    Row {
        OutlinedTextField(
            value = time.renderDate(),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(end = 4.dp)
                .clickable(onClick = { dateDialogState.show() }),
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
                .clickable(onClick = { timeDialogState.show() }),
            readOnly = true,
            enabled = false,
            label = { Text(timeLabel) }
        )
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier,
    contentDescription: String,
    imageVector: ImageVector,
    color: Color,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(96.dp),
            tint = color,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FinishedSliceDetailedViewPreview() {
    TimeTrackerAppTheme {
        SliceDetailedView(
            slice = WorkSlice(
                id = UUID.randomUUID(),
                project = Project("my project"),
                task = Task("my task"),
                description = Description("my work"),
                startInstant = testInstant("2022-01-26T11:30"),
                finishInstant = testInstant("2022-01-26T13:00"),
                duration = 1.hours,
                state = WorkSlice.State.FINISHED,
            ),
            sliceInfoUpdates = emptySliceInfoUpdates,
            runningSliceUpdates = emptyRunningSliceUpdates,
            onBackClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DetailedViewPreviewDark"
)
@Composable
fun RunningSliceDetailedViewPreview() {
    TimeTrackerAppTheme {
        SliceDetailedView(
            slice = WorkSlice(
                id = UUID.randomUUID(),
                project = Project("my project"),
                task = Task("my task"),
                description = Description("my work"),
                startInstant = testInstant("2022-01-26T11:30"),
                finishInstant = testInstant("2022-01-26T13:00"),
                duration = 50.minutes,
                state = WorkSlice.State.PAUSED,
            ),
            sliceInfoUpdates = emptySliceInfoUpdates,
            runningSliceUpdates = emptyRunningSliceUpdates,
            onBackClicked = {},
        )
    }
}