package com.github.jetbrains.timetracker.androidapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.model.WorkSlice
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.github.jetbrains.timetracker.androidapp.util.createTestSlices
import com.github.jetbrains.timetracker.androidapp.util.renderDuration
import com.github.jetbrains.timetracker.androidapp.util.renderTime
import java.util.*
import kotlin.time.Duration

@Composable
fun SliceListView(
    slices: List<WorkSlice>,
    onCardClicked: (id: UUID) -> Unit,
    onSimilarSliceStarted: (id: UUID) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        items(slices) { slice ->
            SliceCard(
                slice = slice,
                duration = slice.duration,
                onCardClicked = onCardClicked,
                onStartClicked = onSimilarSliceStarted,
            )
        }
    }
}

@Composable
fun SliceCard(
    slice: WorkSlice,
    duration: Duration,
    onCardClicked: (id: UUID) -> Unit,
    onStartClicked: (id: UUID) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
            .fillMaxWidth(),
        elevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .clickable(onClick = { onCardClicked(slice.id) })
                    .fillMaxWidth(0.8f)
            ) {
                Row {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        style = MaterialTheme.typography.h6,
                        text = "${slice.startInstant.renderTime()} - ${slice.finishInstant.renderTime()}",
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.h6,
                        text = duration.renderDuration(slice.state),
                    )
                }
                DescriptionText(slice)
                if (slice.project.value.isNotEmpty()) {
                    ProjectText(slice)
                }
            }
            IconButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onStartClicked(slice.id) }
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    modifier = Modifier.size(32.dp),
                    contentDescription = "Start",
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
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

@Preview(showBackground = true)
@Composable
fun SliceCardPreview() {
    TimeTrackerAppTheme {
        val slice = createTestSlices().first()
        SliceCard(
            slice = slice,
            duration = slice.duration,
            onCardClicked = {},
            onStartClicked = {},
        )
    }
}