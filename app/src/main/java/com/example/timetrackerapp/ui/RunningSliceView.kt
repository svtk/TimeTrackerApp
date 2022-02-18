package com.example.timetrackerapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun RunningSliceView(
    runningSliceViewModel: RunningSliceViewModel,
    navigateToHome: () -> Unit,
) {
    val runningSlice by runningSliceViewModel.slice.collectAsState(initial = null)
    if (runningSlice == null) {
        LoadingView()
        return
    }
    SliceDetailedView(
        slice = runningSlice!!,
        sliceInfoUpdates = SliceInfoUpdates(
            onProjectChanged = runningSliceViewModel::onProjectChanged,
            onTaskChanged = runningSliceViewModel::onTaskChanged,
            onDescriptionChanged = runningSliceViewModel::onDescriptionChanged,
            onStartDateChange = runningSliceViewModel::onStartDateChanged,
            onStartTimeChange = runningSliceViewModel::onStartTimeChanged,
            onFinishDateChange = {},
            onFinishTimeChange = {},
            onDurationChange = {},
            onSave = runningSliceViewModel::onSave,
        ),
        runningSliceUpdates = RunningSliceUpdates(
            onPauseClicked = runningSliceViewModel::onPauseClicked,
            onResumeClicked = runningSliceViewModel::onResumeClicked,
            onFinishClicked = {
                runningSliceViewModel.onFinishClicked()
                navigateToHome()
            },
        ),
        onBackClicked = {
            navigateToHome()
            runningSliceViewModel.onDiscard()
        },
    )
}