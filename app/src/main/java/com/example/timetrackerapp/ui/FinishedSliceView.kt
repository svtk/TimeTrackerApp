package com.example.timetrackerapp.ui

import androidx.compose.runtime.Composable
import java.util.*

@Composable
fun FinishedSliceView(
    id: UUID,
    finishedSliceViewModel: FinishedSliceViewModel,
    navigateToHome: () -> Unit,
) {
    finishedSliceViewModel.updateChosenSlice(id)
    if (finishedSliceViewModel.slice == null) {
        LoadingView()
        return
    }
    SliceDetailedView(
        slice = finishedSliceViewModel.slice!!,
        sliceInfoUpdates = SliceInfoUpdates(
            onProjectChanged = finishedSliceViewModel::onProjectChanged,
            onTaskChanged = finishedSliceViewModel::onTaskChanged,
            onDescriptionChanged = finishedSliceViewModel::onDescriptionChanged,
            onStartDateChange = finishedSliceViewModel::onStartDateChanged,
            onStartTimeChange = finishedSliceViewModel::onStartTimeChanged,
            onFinishDateChange = finishedSliceViewModel::onFinishDateChanged,
            onFinishTimeChange = finishedSliceViewModel::onFinishTimeChanged,
            onDurationChange = finishedSliceViewModel::onDurationChanged,
            onSave = finishedSliceViewModel::onSave,
        ),
        runningSliceUpdates = emptyRunningSliceUpdates,
        onBackClicked = {
            navigateToHome()
            finishedSliceViewModel.onDiscard()
        },
    )

}