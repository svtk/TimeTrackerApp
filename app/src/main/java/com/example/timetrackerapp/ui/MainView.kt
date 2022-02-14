package com.example.timetrackerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timetrackerapp.model.*
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme
import kotlin.time.Duration


@Composable
fun MainView(
    slice: WorkSlice?,
    duration: Duration?,
    finishedSlices: List<WorkSlice>,
    currentDescription: String,
    onDescriptionUpdate: (String) -> Unit,
    onNewSlice: () -> Unit,
    onCardClicked: (id: Int) -> Unit,
    onSimilarSliceStarted: (id: Int) -> Unit,
    onCurrentSliceResumed: () -> Unit,
    onCurrentSliceFinished: () -> Unit,
) {
    Column {
        Box(modifier = Modifier.padding(8.dp)) {
            if (slice == null) {
                StartingNewSlice(
                    description = currentDescription,
                    onDescriptionUpdate = onDescriptionUpdate,
                    onNewSlice = onNewSlice
                )
            } else {
                SliceCard(
                    slice = slice,
                    duration = duration ?: slice.duration,
                    onCardClicked = onCardClicked,
                    onStartClicked = { onCurrentSliceResumed() },
                    onFinishClicked = { onCurrentSliceFinished() },
                )
            }
        }
        SliceListView(
            slices = finishedSlices,
            onCardClicked = onCardClicked,
            onSimilarSliceStarted = onSimilarSliceStarted,
        )
    }
}

@Composable
fun StartingNewSlice(
    description: String,
    onDescriptionUpdate: (String) -> Unit,
    onNewSlice: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.8f),
            value = description,
            onValueChange = { onDescriptionUpdate(it) },
            label = { Text("I'm working on...") },
        )
        IconButton(
            onClick = onNewSlice,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = "Start"
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenRunningTaskPreview() {
    TimeTrackerAppTheme {
        MainView(
            slice = WorkSlice(
                0,
                Project("my Project"),
                Task("my Task"),
                Description("my description"),
                WorkSlice.State.RUNNING,
                testTimeIntervals(),
            ),
            duration = null,
            finishedSlices = createTestSlices(),
            currentDescription = "",
            onDescriptionUpdate = {},
            onNewSlice = {},
            onCardClicked = {},
            onSimilarSliceStarted = {},
            onCurrentSliceResumed = {},
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenChoosingTaskPreview() {
    TimeTrackerAppTheme {
        MainView(
            slice = null,
            duration = null,
            finishedSlices = createTestSlices(),
            currentDescription = "",
            onDescriptionUpdate = {},
            onNewSlice = {},
            onCardClicked = {},
            onSimilarSliceStarted = {},
            onCurrentSliceResumed = {},
        ) {}
    }
}