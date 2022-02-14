package com.example.timetrackerapp.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timetrackerapp.data.SlicesRepository
import com.example.timetrackerapp.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration

class RunningSliceViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    var currentDescription by mutableStateOf("")
        private set

    var slice: WorkSlice? by mutableStateOf(null)
        private set

    // TODO should we pause ticker while there's no running work slice?
    private val ticker = TickHandler(viewModelScope)

    fun updateDescription(newText: String) {
        currentDescription = newText
    }

    val currentDuration: Flow<Duration> =
        ticker
            .tickFlow
            .map { slice?.duration ?: Duration.ZERO }

    fun startNewSlice(
        description: String,
        project: Project,
        task: Task,
    ): Int {
        slice = WorkSlice(
            id = 0,
            project,
            task,
            Description(description),
            WorkSlice.State.RUNNING,
            listOf(OpenTimeInterval())
        )
        viewModelScope.launch {
            repository.updateRunningSlice(slice!!)
        }
        return 0
    }

    private inline fun setState(update: WorkSlice.() -> WorkSlice) {
        slice = slice?.update()
        slice?.let {
            viewModelScope.launch {
                repository.updateRunningSlice(it)
            }
        }
    }

    fun onProjectChanged(projectName: String) {
        setState { copy(project = Project(projectName)) }
    }

    fun onTaskChanged(taskName: String) {
        setState { copy(task = Task(taskName)) }
    }

    fun onDescriptionChanged(description: String) {
        setState { copy(description = Description(description)) }
    }

    fun onPauseClicked() {
        setState {
            copy(
                state = WorkSlice.State.PAUSED,
                intervals = intervals.closeLast(),
            )
        }
    }

    fun onResumeClicked() {
        setState {
            copy(
                state = WorkSlice.State.RUNNING,
                intervals = intervals + OpenTimeInterval()
            )
        }
    }

    fun onFinishClicked() {
        setState {
            copy(
                state = WorkSlice.State.FINISHED,
                intervals = intervals.closeLast(),
            )
        }
        slice = null
        viewModelScope.launch {
            repository.finishRunningSlice()
        }
    }

    companion object {
        fun provideFactory(
            repository: SlicesRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return RunningSliceViewModel(repository) as T
                }
            }
        }
    }
}