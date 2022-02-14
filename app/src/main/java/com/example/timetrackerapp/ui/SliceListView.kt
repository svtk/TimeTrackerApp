package com.example.timetrackerapp.ui

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timetrackerapp.model.*
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration

@Composable
fun SliceListView(
    slices: List<WorkSlice>,
    onCardClicked: (id: Int) -> Unit,
    onSimilarSliceStarted: (id: Int) -> Unit,
) {
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        items(slices) { slice ->
            SliceCard(
                slice = slice,
                duration = slice.duration,
                onCardClicked = onCardClicked,
                onStartClicked = onSimilarSliceStarted,
                onFinishClicked = { },
            )
        }
    }
}

@Composable
fun SliceCard(
    slice: WorkSlice,
    duration: Duration,
    onCardClicked: (id: Int) -> Unit,
    onStartClicked: (id: Int) -> Unit,
    onFinishClicked: (id: Int) -> Unit,
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
                    .clickable(onClick = { onCardClicked(slice.id) })
                    .fillMaxWidth(0.8f)
            ) {
                Row {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        style = MaterialTheme.typography.body1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        text = if (slice.project.value.isNotEmpty())
                            slice.project.value +
                                    if (slice.task.value.isNotEmpty()) ": ${slice.task.value}" else ""
                        else "(no project)",
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.body1,
                        text = duration.renderDuration(slice.state),
                    )
                }
                Row {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        style = MaterialTheme.typography.caption,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        text = slice.description.value,
                    )
                    val finishTimeText =
                        if (slice.state == WorkSlice.State.FINISHED)
                            slice.finishTime.renderTime()
                        else
                            "..."
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.caption,
                        text = "${slice.startTime.renderTime()} - $finishTimeText",
                    )
                }
            }
            when (slice.state) {
                WorkSlice.State.RUNNING -> {
                    IconButton(onClick = { onFinishClicked(slice.id) }) {
                        Icon(
                            imageVector = Icons.Filled.StopCircle,
                            contentDescription = "Finish",
                        )
                    }
                }
                WorkSlice.State.PAUSED -> {
                    IconButton(onClick = { onStartClicked(slice.id) }) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = "Resume",
                        )
                    }
                }
                WorkSlice.State.FINISHED -> {
                    IconButton(onClick = { onStartClicked(slice.id) }) {
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
fun SliceListPreview() {
    TimeTrackerAppTheme {
        SliceListView(
            slices = createTestSlices(),
            onCardClicked = {},
            onSimilarSliceStarted = {},
        )
    }
}

fun testTimeInterval(isoDate: String, duration: Duration) =
    ClosedTimeInterval(
        LocalDateTime.parse(isoDate).toInstant(
            TimeZone.currentSystemDefault()
        ), duration
    )

fun createTestSlices() = listOf(
    WorkSlice(
        id = 0,
        project = Project("project 1"),
        task = Task("task 1"),
        description = Description("short description"),
        state = WorkSlice.State.FINISHED,
        intervals = listOf(
            testTimeInterval("2022-01-05T10:00", Duration.minutes(10)),
            testTimeInterval("2022-01-05T10:20", Duration.minutes(15)),
        ),
    ),
    WorkSlice(
        id = 1,
        project = Project("project 2 - very long title"),
        task = Task("task 2 - also long"),
        description = Description("very long second description"),
        state = WorkSlice.State.FINISHED,
        intervals = listOf(
            testTimeInterval("2022-01-26T11:30", Duration.minutes(20)),
            testTimeInterval("2022-01-26T13:00", Duration.minutes(30)),
        ),
    ),
)
