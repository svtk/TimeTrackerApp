package com.example.timetrackerapp.model


import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

class RunningBlockOfWorkStore(
    coroutineScope: CoroutineScope,
    private val nextId: () -> Int
) {
    var blockOfWork: BlockOfWork? by mutableStateOf(null)
        private set

    // TODO should we pause ticker while there's no running time block?
    private val ticker = TickHandler(coroutineScope)

    @Composable
    fun getCurrentDuration(): State<Duration> = ticker
        .tickFlow
        .map { blockOfWork?.duration ?: Duration.ZERO }
        .distinctUntilChanged()
        .collectAsState(Duration.ZERO)

    fun clearTimeBlock() {
        blockOfWork = null
    }

    fun startNewTimeBlock(
        description: String,
        project: Project,
        task: Task,
    ): Int {
        val id = nextId()
        blockOfWork = BlockOfWork(
            id,
            project,
            task,
            Description(description),
            BlockOfWork.State.RUNNING,
            listOf(OpenTimeInterval())
        )
        return id
    }

    private inline fun setState(update: BlockOfWork.() -> BlockOfWork) {
        blockOfWork = blockOfWork?.update()
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
    }
}