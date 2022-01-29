package com.example.timetrackerapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timetrackerapp.model.*
import com.example.timetrackerapp.model.Task
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme
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
        OutlinedTextField(
            value = blockOfWork.description.value,
            onValueChange = onDescriptionChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Description") }
        )
        OutlinedTextField(
            value = blockOfWork.project.value,
            onValueChange = onProjectChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Project") },
        )
        OutlinedTextField(
            value = blockOfWork.task.value,
            onValueChange = onTaskChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Task") }
        )
        if (blockOfWork.state != BlockOfWork.State.CREATED) {
            Row {
                OutlinedTextField(
                    value = blockOfWork.startTime.renderDate(),
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(0.5f),
                    readOnly = true,
                    label = { Text("Start date") }
                )
                OutlinedTextField(
                    value = blockOfWork.startTime.renderTime(),
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                    readOnly = true,
                    label = { Text("Start time") }
                )
            }
            if (blockOfWork.state == BlockOfWork.State.FINISHED) {
                Box {
                    OutlinedTextField(
                        value = "${duration.inWholeMinutes}",
                        onValueChange = onTaskChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Duration") }
                    )
                    Text(
                        text = "min",
                        modifier = Modifier.align(Alignment.CenterEnd)
                            .padding(end = 14.dp, top = 3.dp),
                    )
                }
            } else {
                OutlinedTextField(
                    value = duration.renderDuration(),
                    onValueChange = onTaskChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Duration") }
                )
            }
            if (blockOfWork.state == BlockOfWork.State.FINISHED) {
                Row {
                    OutlinedTextField(
                        value = blockOfWork.finishTime.renderDate(),
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(0.5f),
                        readOnly = true,
                        label = { Text("End date") }
                    )
                    OutlinedTextField(
                        value = blockOfWork.finishTime.renderTime(),
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                        readOnly = true,
                        label = { Text("End time") }
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (blockOfWork.state == BlockOfWork.State.CREATED) {
                OutlinedButton(onClick = onStartClicked) {
                    Text("START")
                }
            }
            if (blockOfWork.state == BlockOfWork.State.PAUSED) {
                OutlinedButton(onClick = onResumeClicked) {
                    Text("RESUME")
                }
            } else {
                OutlinedButton(onClick = onPauseClicked) {
                    Text("PAUSE")
                }
            }
            OutlinedButton(onClick = onFinishClicked) {
                Text("FINISH")
            }
        }
        OutlinedButton(onClick = onBackClicked) {
            Text("BACK")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BlockOfWorkDetailedViewPreview() {
    TimeTrackerAppTheme {
        BlockOfWorkDetailedView(
            blockOfWork = BlockOfWork(
                id = 0,
                project = Project("my project"),
                task = Task("my task"),
                description = Description("my work"),
                state = BlockOfWork.State.FINISHED,
                intervals = listOf(
                    testTimeInterval("2022-01-26T11:30", Duration.minutes(20)),
                    testTimeInterval("2022-01-26T13:00", Duration.minutes(30)),
                )
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