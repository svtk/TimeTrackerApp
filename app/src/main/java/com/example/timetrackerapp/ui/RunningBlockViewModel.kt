package com.example.timetrackerapp.model

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timetrackerapp.data.BlocksOfWorkRepository
import com.example.timetrackerapp.ui.FinishedBlocksViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration

class RunningBlockViewModel(
    private val repository: BlocksOfWorkRepository
) : ViewModel() {

    var currentDescription by mutableStateOf("")
        private set

    var blockOfWork: BlockOfWork? by mutableStateOf(null)
        private set

    // TODO should we pause ticker while there's no running time block?
    private val ticker = TickHandler(viewModelScope)

    fun updateDescription(newText: String) {
        currentDescription = newText
    }

    @Composable
    fun getCurrentDuration(): State<Duration> = ticker
        .tickFlow
        .map { blockOfWork?.duration ?: Duration.ZERO }
        .distinctUntilChanged()
        .collectAsState(Duration.ZERO)

    fun startNewTimeBlock(
        description: String,
        project: Project,
        task: Task,
    ): Int {
        blockOfWork = BlockOfWork(
            id = 0,
            project,
            task,
            Description(description),
            BlockOfWork.State.RUNNING,
            listOf(OpenTimeInterval())
        )
        viewModelScope.launch {
            repository.updateRunningBlock(blockOfWork!!)
        }
        return 0
    }

    private inline fun setState(update: BlockOfWork.() -> BlockOfWork) {
        blockOfWork = blockOfWork?.update()
        blockOfWork?.let {
            viewModelScope.launch {
                repository.updateRunningBlock(it)
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

    fun onStartClicked() {
        setState {
            copy(
                state = BlockOfWork.State.RUNNING,
                intervals = listOf(OpenTimeInterval()),
            )
        }
    }

    fun onPauseClicked() {
        setState {
            copy(
                state = BlockOfWork.State.PAUSED,
                intervals = intervals.closeLast(),
            )
        }
    }

    fun onResumeClicked() {
        setState {
            copy(
                state = BlockOfWork.State.RUNNING,
                intervals = intervals + OpenTimeInterval()
            )
        }
    }

    fun onFinishClicked() {
        setState {
            copy(
                state = BlockOfWork.State.FINISHED,
                intervals = intervals.closeLast(),
            )
        }
        blockOfWork = null
        viewModelScope.launch {
            repository.finishRunningBlock()
        }
    }

    companion object {
        fun provideFactory(
            repository: BlocksOfWorkRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return RunningBlockViewModel(repository) as T
                }
            }
        }
    }
}