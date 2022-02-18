package com.example.timetrackerapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timetrackerapp.data.SlicesRepository
import com.example.timetrackerapp.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.seconds

class RunningSliceViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    private var sliceChanges: SliceChanges? by mutableStateOf(null)

    // TODO pause ticker when view isn't active
    private val tickHandler = TickHandler(viewModelScope, 1.seconds)

    val slice: Flow<WorkSlice?> =
        repository
            .observeRunningSlice()
            .convertToWorkSliceAndEmitEverySecond(viewModelScope, tickHandler.tickFlow)
            .map { it?.applyChanges(sliceChanges) }

    private inline fun recordChanges(update: SliceChanges.() -> SliceChanges) {
        sliceChanges = (sliceChanges ?: SliceChanges()).update()
    }

    fun onSave() {
        sliceChanges?.let { changes ->
            viewModelScope.launch {
                repository.updateRunningSlice(changes)
            }
        }
        sliceChanges = null
    }

    fun onDiscard() {
        sliceChanges = null
    }

    fun onStartDateChanged(newDate: LocalDate) {
        recordChanges { copy(newStartDate = newDate) }
        onSave()
    }

    fun onStartTimeChanged(newTime: LocalDateTime) {
        recordChanges { copy(newStartTime = newTime) }
        onSave()
    }

    fun onProjectChanged(projectName: String) {
        recordChanges { copy(newProject = Project(projectName)) }
    }

    fun onTaskChanged(taskName: String) {
        recordChanges { copy(newTask = Task(taskName)) }
    }

    fun onDescriptionChanged(description: String) {
        recordChanges { copy(newDescription = Description(description)) }
    }

    fun onPauseClicked() {
        onSave()
        viewModelScope.launch {
            repository.changeRunningSliceState(WorkSlice.State.PAUSED)
        }
    }

    fun onResumeClicked() {
        onSave()
        viewModelScope.launch {
            repository.changeRunningSliceState(WorkSlice.State.RUNNING)
        }
    }

    fun onFinishClicked() {
        onSave()
        viewModelScope.launch {
            repository.finishRunningSlice()
        }
        sliceChanges = null
    }

    companion object {
        fun provideFactory(
            repository: SlicesRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RunningSliceViewModel(repository) as T
                }
            }
        }
    }
}

fun Flow<RunningSlice?>.convertToWorkSliceAndEmitEverySecond(
    scope: CoroutineScope,
    tickFlow: Flow<Unit>,
) =
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