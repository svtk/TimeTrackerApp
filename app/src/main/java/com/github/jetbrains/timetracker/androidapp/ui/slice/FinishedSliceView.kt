package com.github.jetbrains.timetracker.androidapp.ui.slice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.jetbrains.timetracker.androidapp.model.applyChanges
import com.github.jetbrains.timetracker.androidapp.ui.util.LoadingView
import org.koin.androidx.compose.getViewModel
import java.util.*

@Composable
fun FinishedSliceView(
    id: UUID,
    finishedSliceViewModel: FinishedSliceViewModel = getViewModel(),
) {
    val sliceChangesState = remember { SliceChangesState() }
    finishedSliceViewModel.updateChosenSlice(id)
    if (finishedSliceViewModel.slice == null) {
        LoadingView()
        return
    }
    SliceDetailedView(
        slice = finishedSliceViewModel.slice!!.applyChanges(sliceChangesState.changes),
        sliceInfoUpdates = SliceInfoUpdates(
            onProjectChanged = sliceChangesState::onProjectChanged,
            onTaskChanged = sliceChangesState::onTaskChanged,
            onDescriptionChanged = sliceChangesState::onDescriptionChanged,
            onStartDateChange = sliceChangesState::onStartDateChanged,
            onStartTimeChange = sliceChangesState::onStartTimeChanged,
            onFinishDateChange = sliceChangesState::onFinishDateChanged,
            onFinishTimeChange = sliceChangesState::onFinishTimeChanged,
            onDurationChange = sliceChangesState::onDurationChanged,
            onSave = { finishedSliceViewModel.onSave(sliceChangesState.changes) },
        ),
        runningSliceUpdates = emptyRunningSliceUpdates,
    )
}