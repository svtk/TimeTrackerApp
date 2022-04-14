package com.github.jetbrains.timetracker.androidapp.ui.slice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.jetbrains.timetracker.androidapp.model.applyChanges
import com.github.jetbrains.timetracker.androidapp.model.isNotEmpty
import com.github.jetbrains.timetracker.androidapp.ui.home.NewSliceView
import org.koin.androidx.compose.getViewModel

@Composable
fun RunningSliceView(
    runningSliceViewModel: RunningSliceViewModel = getViewModel(),
    navigateToRunningSlice: () -> Unit,
) {
    val runningSlice by runningSliceViewModel.slice.collectAsState(initial = null)
    val sliceChangesState = remember { SliceChangesState() }
    if (runningSlice == null) {
        NewSliceView(
            navigateToRunningSlice = navigateToRunningSlice,
        )
        return
    }
    fun onSave() {
        runningSliceViewModel.onSave(sliceChangesState.changes)
        sliceChangesState.clearChanges()
    }
    SliceDetailedView(
        slice = runningSlice!!.applyChanges(sliceChangesState.changes),
        sliceInfoUpdates = SliceInfoUpdates(
            onProjectChanged = sliceChangesState::onProjectChanged,
            onTaskChanged = sliceChangesState::onTaskChanged,
            onDescriptionChanged = sliceChangesState::onDescriptionChanged,
            onStartDateChange = {
                sliceChangesState.onStartDateChanged(it)
                onSave()
            },
            onStartTimeChange = {
                sliceChangesState.onStartTimeChanged(it)
                onSave()
            },
            onFinishDateChange = {},
            onFinishTimeChange = {},
            onDurationChange = {},
            onSave = ::onSave,
        ),
        runningSliceUpdates = RunningSliceUpdates(
            onPauseClicked = runningSliceViewModel::onPauseClicked,
            onResumeClicked = runningSliceViewModel::onResumeClicked,
            onFinishClicked = runningSliceViewModel::onFinishClicked,
        ),
        isEdited = sliceChangesState.changes.isNotEmpty(),
    )
}