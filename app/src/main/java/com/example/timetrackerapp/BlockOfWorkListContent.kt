package com.example.timetrackerapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.timetrackerapp.model.*
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration

@Composable
fun BlockOfWorkListContent(
    blocks: List<BlockOfWork>,
    onBlockClicked: (id: Int) -> Unit
) {
    LazyColumn {
        items(blocks) { block ->
            BlockOfWorkCard(
                blockOfWork = block,
                onCardClicked = onBlockClicked,
                onStartClicked = { },
                onPauseClicked = { },
                onResumeClicked = { },
                onFinishClicked = { },
            )
        }
    }
}

@Composable
fun BlockOfWorkCard(
    blockOfWork: BlockOfWork,
    onCardClicked: (id: Int) -> Unit,
    onStartClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onFinishClicked: () -> Unit,
) {
    Card {
        Column(modifier = Modifier.clickable(onClick = { onCardClicked(blockOfWork.id) })) {
            Text(text = "${blockOfWork.project.value}: ${blockOfWork.task.value}")
            Text(text = blockOfWork.description.value)
            val finishTimeText =
                if (blockOfWork.state == BlockOfWork.State.FINISHED)
                    blockOfWork.finishTime.renderTime()
                else
                    "..."
            Text(text = "${blockOfWork.startTime.renderTime()} - $finishTimeText")
            Text(text = blockOfWork.duration.renderDuration())
            when (blockOfWork.state) {
                BlockOfWork.State.RUNNING -> {
                    IconButton(onClick = onFinishClicked) {
                        Icon(
                            imageVector = Icons.Filled.StopCircle,
                            contentDescription = "Finish"
                        )
                    }
                }
                BlockOfWork.State.PAUSED -> {
                    IconButton(onClick = onResumeClicked) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = "Resume"
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BlockOfWorkListPreview() {
    TimeTrackerAppTheme {
        BlockOfWorkListContent(
            blocks = listOf(
                BlockOfWork(
                    id = 0,
                    project = Project("project 1"),
                    task = Task("task 1"),
                    description = Description("description 1"),
                    intervals = listOf(
                        testTimeInterval("2022-01-05T10:00", Duration.minutes(10)),
                        testTimeInterval("2022-01-05T10:20", Duration.minutes(15)),
                    ),
                    state = BlockOfWork.State.FINISHED,
                ),
                BlockOfWork(
                    id = 1,
                    project = Project("project 2"),
                    task = Task("task 2"),
                    description = Description("description 2"),
                    intervals = listOf(
                        testTimeInterval("2022-01-26T11:30", Duration.minutes(20)),
                        testTimeInterval("2022-01-26T13:00", Duration.minutes(30)),
                    ),
                    state = BlockOfWork.State.FINISHED,
                ),
            ),
            onBlockClicked = {}
        )
    }
}

fun testTimeInterval(isoDate: String, duration: Duration) =
    ClosedTimeInterval(
        LocalDateTime.parse(isoDate).toInstant(
            TimeZone.currentSystemDefault()
        ), duration
    )