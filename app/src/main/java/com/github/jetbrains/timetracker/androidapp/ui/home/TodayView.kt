package com.github.jetbrains.timetracker.androidapp.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.model.*
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.github.jetbrains.timetracker.androidapp.util.createTestInstant
import com.github.jetbrains.timetracker.androidapp.util.createTestSlicesByDays
import org.koin.androidx.compose.getViewModel
import java.util.*
import kotlin.time.Duration.Companion.minutes

@Composable
fun TodayView(
    timerViewModel: TimerViewModel = getViewModel(),
    logsViewModel: LogsViewModel = getViewModel(),
    navigateToRunningSlice: () -> Unit,
    navigateToChosenSlice: (UUID) -> Unit,
) {
    val finishedSlicesByDays by logsViewModel.todaySlices.collectAsState()
    val runningSlice by timerViewModel.runningSlice.collectAsState(null)
    TodayView(
        slice = runningSlice,
        finishedSlicesByDays = finishedSlicesByDays,
        onCardClicked = navigateToChosenSlice,
        onCurrentSliceClicked = navigateToRunningSlice,
        onCurrentSliceResumed = timerViewModel::resumeRunningSlice,
        onCurrentSliceFinished = timerViewModel::finishRunningSlice,
    )
}

@Composable
fun TodayView(
    slice: WorkSlice?,
    finishedSlicesByDays: WorkSlicesByDays,
    onCardClicked: (id: UUID) -> Unit,
    onCurrentSliceClicked: () -> Unit,
    onCurrentSliceResumed: () -> Unit,
    onCurrentSliceFinished: () -> Unit,
) {
    Column {
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
                slicesByDays = finishedSlicesByDays,
                onCardClicked = onCardClicked,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodayScreenRunningTaskPreview() {
    TimeTrackerAppTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            TodayView(
                slice = WorkSlice(
                    UUID.randomUUID(),
                    activity = WorkActivity(
                        Project("my Project"),
                        Task("my Task"),
                        Description("my description"),
                    ),
                    startInstant = createTestInstant("2022-01-26T11:30"),
                    finishInstant = createTestInstant("2022-01-26T13:00"),
                    duration = 50.minutes,
                    state = WorkSlice.State.RUNNING,
                ),
                finishedSlicesByDays = createTestSlicesByDays(),
                onCardClicked = {},
                onCurrentSliceClicked = {},
                onCurrentSliceResumed = {},
                onCurrentSliceFinished = {},
            )
        }
    }
}

/*
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
                finishedSlicesByDays = createTestSlices(),
                onCardClicked = {},
                onCurrentSliceClicked = {},
                onCurrentSliceResumed = {},
                onCurrentSliceFinished = {},
            )
        }
    }
}*/
