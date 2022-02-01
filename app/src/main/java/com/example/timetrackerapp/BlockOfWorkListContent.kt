package com.example.timetrackerapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        items(blocks) { block ->
            BlockOfWorkCard(
                blockOfWork = block,
                duration = block.duration,
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
    duration: Duration,
    onCardClicked: (id: Int) -> Unit,
    onStartClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onFinishClicked: () -> Unit,
) {
    Card(
        modifier = Modifier.padding(4.dp),
        elevation = 4.dp,
    ) {
        Row {
            Column(
                modifier =
                Modifier
                    .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)
                    .clickable(onClick = { onCardClicked(blockOfWork.id) })
                    .fillMaxWidth(0.8f)
            ) {
                Row {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        style = MaterialTheme.typography.body1,
                        text = if (blockOfWork.project.value.isNotEmpty())
                            blockOfWork.project.value +
                                    if (blockOfWork.task.value.isNotEmpty()) ": ${blockOfWork.task.value}" else ""
                        else "(no project)",
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.body1,
                        text = duration.renderDuration(blockOfWork.state),
                    )
                }
                Row {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        style = MaterialTheme.typography.caption,
                        text = blockOfWork.description.value,
                    )
                    val finishTimeText =
                        if (blockOfWork.state == BlockOfWork.State.FINISHED)
                            blockOfWork.finishTime.renderTime()
                        else
                            "..."
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.caption,
                        text = "${blockOfWork.startTime.renderTime()} - $finishTimeText",
                    )
                }
            }
            when (blockOfWork.state) {
                BlockOfWork.State.RUNNING -> {
                    IconButton(onClick = onFinishClicked) {
                        Icon(
                            imageVector = Icons.Filled.StopCircle,
                            contentDescription = "Finish",
                        )
                    }
                }
                BlockOfWork.State.PAUSED -> {
                    IconButton(onClick = onResumeClicked) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = "Resume",
                        )
                    }
                }
                else -> {
                    IconButton(onClick = onResumeClicked) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = "Start",
                            tint = Color.Gray,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BlockOfWorkListPreview() {
    TimeTrackerAppTheme {
        BlockOfWorkListContent(
            blocks = testTimeBlocks(),
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

fun testTimeBlocks() = listOf(
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
)
