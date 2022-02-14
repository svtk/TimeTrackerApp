package com.example.timetrackerapp

import androidx.compose.foundation.layout.*
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
import com.example.timetrackerapp.model.Task
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

@Composable
fun BlockOfWorkDetailedView(
    blockOfWork: BlockOfWork,
    duration: Duration,
    onProjectChanged: (String) -> Unit,
    onTaskChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onStartClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onFinishClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    Column(Modifier.padding(8.dp)) {
        BlockOfWorkItem(
            "Description", blockOfWork.description.value, onDescriptionChanged
        )
        BlockOfWorkItem(
            "Project", blockOfWork.project.value, onProjectChanged
        )
        BlockOfWorkItem(
            "Task", blockOfWork.task.value, onTaskChanged
        )
        if (blockOfWork.state != BlockOfWork.State.CREATED) {
            DateTimeFields(
                blockOfWork.startTime, "Start date", "Start time"
            )
            if (blockOfWork.state == BlockOfWork.State.FINISHED) {
                BlockOfWorkItem(
                    "Duration", duration.renderDurationFinished(), {}
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
            if (blockOfWork.state == BlockOfWork.State.FINISHED) {
                DateTimeFields(
                    blockOfWork.finishTime, "End date", "End time"
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (blockOfWork.state) {
                BlockOfWork.State.CREATED -> {
                    OutlinedButton(onClick = onStartClicked) {
                        Text("START")
                    }
                }
                BlockOfWork.State.PAUSED -> {
                    TwoButtons(
                        leftTitle = "RESUME",
                        rightTitle = "FINISH",
                        onLeftClicked = onResumeClicked,
                        onRightClicked = onFinishClicked
                    )
                }
                BlockOfWork.State.RUNNING -> {
                    TwoButtons(
                        leftTitle = "PAUSE",
                        rightTitle = "FINISH",
                        onLeftClicked = onPauseClicked,
                        onRightClicked = onFinishClicked
                    )
                }
                BlockOfWork.State.FINISHED -> {}
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
private fun BlockOfWorkItem(
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
    timeLabel: String
) {
    Row {
        OutlinedTextField(
            value = time.renderDate(),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(end = 4.dp),
            readOnly = true,
            label = { Text(dateLabel) }
        )
        OutlinedTextField(
            value = time.renderTime(),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
            readOnly = true,
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
fun FinishedBlockDetailedViewPreview() {
    TimeTrackerAppTheme {
        BlockOfWorkDetailedView(
            blockOfWork = BlockOfWork(
                id = 0,
                project = Project("my project"),
                task = Task("my task"),
                description = Description("my work"),
                state = BlockOfWork.State.FINISHED,
                intervals = testTimeIntervals()
            ),
            duration = Duration.minutes(50),
            onProjectChanged = {},
            onTaskChanged = {},
            onDescriptionChanged = {},
            onStartClicked = {},
            onPauseClicked = {},
            onResumeClicked = {},
            onFinishClicked = {},
            onBackClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RunningBlockDetailedViewPreview() {
    TimeTrackerAppTheme {
        BlockOfWorkDetailedView(
            blockOfWork = BlockOfWork(
                id = 0,
                project = Project("my project"),
                task = Task("my task"),
                description = Description("my work"),
                state = BlockOfWork.State.PAUSED,
                intervals = testTimeIntervals()
            ),
            duration = Duration.minutes(50),
            onProjectChanged = {},
            onTaskChanged = {},
            onDescriptionChanged = {},
            onStartClicked = {},
            onPauseClicked = {},
            onResumeClicked = {},
            onFinishClicked = {},
            onBackClicked = {}
        )
    }
}


@Composable
fun testTimeIntervals() = listOf(
    testTimeInterval("2022-01-26T11:30", Duration.minutes(20)),
    testTimeInterval("2022-01-26T13:00", Duration.minutes(30)),
)