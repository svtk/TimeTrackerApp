package com.github.jetbrains.timetracker.androidapp.ui.slice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jetbrains.timetracker.androidapp.data.SlicesRepository
import com.github.jetbrains.timetracker.androidapp.model.RunningSlice
import com.github.jetbrains.timetracker.androidapp.model.SliceChanges
import com.github.jetbrains.timetracker.androidapp.model.WorkSlice
import com.github.jetbrains.timetracker.androidapp.ui.util.TickHandler
import com.github.jetbrains.timetracker.androidapp.ui.util.transformAndEmitRegularly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class RunningSliceViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    private val tickHandler = TickHandler(viewModelScope, 1.seconds)

    val slice: Flow<WorkSlice?> =
        repository
            .observeRunningSlice()
            .convertToWorkSliceAndEmitEverySecond(viewModelScope, tickHandler.tickFlow)

    fun onSave(changes: SliceChanges) {
            viewModelScope.launch {
                repository.updateRunningSlice(changes)
            }
    }
    fun onPauseClicked() {
        viewModelScope.launch {
            repository.changeRunningSliceState(WorkSlice.State.PAUSED)
        }
    }

    fun onResumeClicked() {
        viewModelScope.launch {
            repository.changeRunningSliceState(WorkSlice.State.RUNNING)
        }
    }

    fun onFinishClicked() {
        viewModelScope.launch {
            repository.finishRunningSlice()
        }
    }
}

fun Flow<RunningSlice?>.convertToWorkSliceAndEmitEverySecond(
    scope: CoroutineScope,
    tickFlow: Flow<Unit>,
): Flow<WorkSlice?> =
    transformAndEmitRegularly(
        scope = scope,
        tickFlow = tickFlow,
        transform = {
            it?.let { runningSlice ->
                WorkSlice(
                    id = runningSlice.id,
                    project = runningSlice.project,
                    task = runningSlice.task,
                    description = runningSlice.description,
                    startInstant = runningSlice.startInstant,
                    // we update duration and finish time every second:
                    finishInstant = runningSlice.countCurrentFinishInstant(),
                    duration = runningSlice.countCurrentDuration(),
                    state = if (runningSlice.isPaused) WorkSlice.State.PAUSED else WorkSlice.State.RUNNING
                )
            }
        },
    )