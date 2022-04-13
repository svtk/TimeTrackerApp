package com.github.jetbrains.timetracker.androidapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jetbrains.timetracker.androidapp.data.SlicesRepository
import com.github.jetbrains.timetracker.androidapp.model.*
import com.github.jetbrains.timetracker.androidapp.ui.slice.convertToWorkSliceAndEmitEverySecond
import com.github.jetbrains.timetracker.androidapp.ui.util.TickHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.Duration.Companion.seconds

class TimerViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    var currentDescription by mutableStateOf("")
        private set

    private val tickHandler = TickHandler(viewModelScope, 1.seconds)
    val runningSlice: Flow<WorkSlice?> =
        repository
            .observeRunningSlice()
            .convertToWorkSliceAndEmitEverySecond(viewModelScope, tickHandler.tickFlow)

    val finishedSlices: StateFlow<List<WorkSlice>> =
        repository.observeFinishedSlices().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val workActivities: StateFlow<List<WorkActivity>> =
        repository.observeWorkActivitiesSuggestions().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    fun updateDescription(newText: String) {
        currentDescription = newText
    }

    private fun startNewSlice(
        description: String, project: Project, task: Task
    ) {
        updateDescription("")
        viewModelScope.launch {
            repository.startRunningSlice(
                Description(description),
                task,
                project,
            )
        }
    }

    fun startNewSlice() = startNewSlice(
        currentDescription,
        Project(""),
        Task(""),
    )

    fun startSimilarActivity(workActivity: WorkActivity) {
        finishRunningSlice()
        viewModelScope.launch {
            startNewSlice(
                workActivity.description.value,
                workActivity.project,
                workActivity.task
            )
        }
    }

    fun resumeRunningSlice() {
        viewModelScope.launch {
            repository.changeRunningSliceState(WorkSlice.State.RUNNING)
        }
    }

    fun finishRunningSlice() {
        viewModelScope.launch {
            repository.finishRunningSlice()
        }
    }
}