package com.example.timetrackerapp.ui.home

import android.content.res.Configuration
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
import com.example.timetrackerapp.model.Description
import com.example.timetrackerapp.model.Project
import com.example.timetrackerapp.model.Task
import com.example.timetrackerapp.model.WorkSlice
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme
import com.example.timetrackerapp.util.*
import java.util.*
import kotlin.time.Duration.Companion.minutes

@Composable
fun HomeView(
    homeViewModel: HomeViewModel,
    navigateToRunningSlice: () -> Unit,
    navigateToChosenSlice: (UUID) -> Unit,
) {
    val finishedSlices by homeViewModel.finishedSlices.collectAsState()
    val runningSlice by homeViewModel.runningSlice.collectAsState(null)
    HomeView(
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
fun HomeView(
    slice: WorkSlice?,
    finishedSlices: List<WorkSlice>,
    currentDescription: String,
    onDescriptionUpdate: (String) -> Unit,
    onNewSlice: () -> Unit,
    onCardClicked: (id: UUID) -> Unit,
    onSimilarSliceStarted: (id: UUID) -> Unit,
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
                RunningSliceCard(
                    slice = slice,
                    duration = slice.duration,
                    onCardClicked = onCurrentSliceClicked,
                    onResumeClicked = onCurrentSliceResumed,
                    onFinishClicked = onCurrentSliceFinished,
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
    Card(Modifier.padding(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                value = description,
                onValueChange = { onDescriptionUpdate(it) },
                label = { Text("I'm working on...") },
                colors = TextFieldDefaults.changeBorderColor(),
            )
            IconButton(
                onClick = onNewSlice,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayCircle,
                    contentDescription = "Start",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colors.secondary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenRunningTaskPreview() {
    TimeTrackerAppTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            HomeView(
                slice = WorkSlice(
                    UUID.randomUUID(),
                    Project("my Project"),
                    Task("my Task"),
                    Description("my description"),
                    startInstant = createTestInstant("2022-01-26T11:30"),
                    finishInstant = createTestInstant("2022-01-26T13:00"),
                    duration = 50.minutes,
                    state = WorkSlice.State.RUNNING,
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
}

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "HomeScreenChoosingTaskPreviewDark"
)
@Composable
fun HomeScreenChoosingTaskPreview() {
    TimeTrackerAppTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            HomeView(
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
}