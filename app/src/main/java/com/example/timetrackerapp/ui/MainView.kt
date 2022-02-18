package com.example.timetrackerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timetrackerapp.model.*
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme
import com.example.timetrackerapp.util.testInstant
import com.example.timetrackerapp.util.testTimeIntervals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun MainView(
    homeViewModel: HomeViewModel,
    navigateToRunningSlice: () -> Unit,
    navigateToChosenSlice: (Int) -> Unit,
) {
    val finishedSlices by homeViewModel.finishedSlices.collectAsState()
    val runningSlice by homeViewModel.
    runningSlice.collectAsState(null)
    MainView(
        slice = runningSlice,
        finishedSlices = finishedSlices,
        currentDescription = homeViewModel.currentDescription,
        onDescriptionUpdate = homeViewModel::updateDescription,
        onNewSlice = {
            homeViewModel.startNewSlice()
            navigateToRunningSlice()
        },
        onCardClicked = navigateToChosenSlice,
        onSimilarSliceStarted = homeViewModel::startSimilarSlice,
        onCurrentSliceClicked = navigateToRunningSlice,
        onCurrentSliceResumed = {}, // TODO
        onCurrentSliceFinished = homeViewModel::finishRunningSlice,
    )
}

@Composable
fun MainView(
    slice: WorkSlice?,
    finishedSlices: List<WorkSlice>,
    currentDescription: String,
    onDescriptionUpdate: (String) -> Unit,
    onNewSlice: () -> Unit,
    onCardClicked: (id: Int) -> Unit,
    onSimilarSliceStarted: (id: Int) -> Unit,
    onCurrentSliceClicked: () -> Unit,
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
                    duration = slice.duration,
                    onCardClicked = { onCurrentSliceClicked() },
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
                startInstant = testInstant("2022-01-26T11:30"),
                finishInstant = testInstant("2022-01-26T13:00"),
                duration = 50.minutes,
                state = WorkSlice.State.PAUSED,
            ),
            finishedSlices = createTestSlices(),
            currentDescription = "",
            onDescriptionUpdate = {},
            onNewSlice = {},
            onCardClicked = {},
            onSimilarSliceStarted = {},
            onCurrentSliceClicked = {},
            onCurrentSliceResumed = {},
            onCurrentSliceFinished = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenChoosingTaskPreview() {
    TimeTrackerAppTheme {
        MainView(
            slice = null,
            finishedSlices = createTestSlices(),
            currentDescription = "",
            onDescriptionUpdate = {},
            onNewSlice = {},
            onCardClicked = {},
            onSimilarSliceStarted = {},
            onCurrentSliceClicked = {},
            onCurrentSliceResumed = {},
            onCurrentSliceFinished = {},
        )
    }
}