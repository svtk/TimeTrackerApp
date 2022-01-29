package com.example.timetrackerapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
    Column(modifier = Modifier.padding(4.dp)) {
        OutlinedTextField(
            value = blockOfWork.project.value,
            onValueChange = onProjectChanged,
            label = { Text("Project") }
        )
        OutlinedTextField(
            value = blockOfWork.task.value,
            onValueChange = onTaskChanged,
            label = { Text("Task") }
        )
        OutlinedTextField(
            value = blockOfWork.description.value,
            onValueChange = onDescriptionChanged,
            label = { Text("Description") }
        )
        if (blockOfWork.state != BlockOfWork.State.CREATED) {
            OutlinedTextField(
                value = blockOfWork.startTime.renderTime(),
                onValueChange = { },
                readOnly = true,
                label = { Text("Start time") }
            )
            if (blockOfWork.state == BlockOfWork.State.FINISHED) {
                OutlinedTextField(
                    value = blockOfWork.finishTime.renderTime(),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Finish time") }
                )
            }
            OutlinedTextField(
                value = duration.renderDuration(),
                onValueChange = onTaskChanged,
                label = { Text("Time passed") }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
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
fun RunningTaskPreview() {
    TimeTrackerAppTheme {
        BlockOfWorkDetailedView(
            blockOfWork = BlockOfWork(
                id = 0,
                project = Project("my project"),
                task = Task("my task"),
                description = Description("my work"),
                state = BlockOfWork.State.RUNNING,
                intervals = listOf()
            ),
            duration = Duration.seconds(100),
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