package com.github.jetbrains.timetracker.androidapp.ui.home

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
import com.github.jetbrains.timetracker.androidapp.model.Description
import com.github.jetbrains.timetracker.androidapp.model.Project
import com.github.jetbrains.timetracker.androidapp.model.Task
import com.github.jetbrains.timetracker.androidapp.model.WorkSlice
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.github.jetbrains.timetracker.androidapp.util.changeBorderColor
import com.github.jetbrains.timetracker.androidapp.util.createTestInstant
import com.github.jetbrains.timetracker.androidapp.util.createTestSlices
import org.koin.androidx.compose.getViewModel
import java.util.*
import kotlin.time.Duration.Companion.minutes

@Composable
fun LogsView(
    timerViewModel: TimerViewModel = getViewModel(),
    navigateToRunningSlice: () -> Unit,
    navigateToChosenSlice: (UUID) -> Unit,
) {
    val finishedSlices by timerViewModel.finishedSlices.collectAsState()
    val runningSlice by timerViewModel.runningSlice.collectAsState(null)
    LogsView(
        slice = runningSlice,
        finishedSlices = finishedSlices,
        onCardClicked = navigateToChosenSlice,
        onCurrentSliceClicked = navigateToRunningSlice,
        onCurrentSliceResumed = timerViewModel::resumeRunningSlice,
        onCurrentSliceFinished = timerViewModel::finishRunningSlice,
    )
}

@Composable
fun LogsView(
    slice: WorkSlice?,
    finishedSlices: List<WorkSlice>,
    onCardClicked: (id: UUID) -> Unit,
    onCurrentSliceClicked: () -> Unit,
    onCurrentSliceResumed: () -> Unit,
    onCurrentSliceFinished: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        if (slice != null) {
            RunningSliceCard(
                slice = slice,
                duration = slice.duration,
                onCardClicked = onCurrentSliceClicked,
                onResumeClicked = onCurrentSliceResumed,
                onFinishClicked = onCurrentSliceFinished,
            )
            Spacer(Modifier.height(16.dp))
        }
        SliceListView(
            slices = finishedSlices,
            onCardClicked = onCardClicked,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenRunningTaskPreview() {
    TimeTrackerAppTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            LogsView(
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
                onCardClicked = {},
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
            LogsView(
                slice = null,
                finishedSlices = createTestSlices(),
                onCardClicked = {},
                onCurrentSliceClicked = {},
                onCurrentSliceResumed = {},
                onCurrentSliceFinished = {},
            )
        }
    }
}