package com.example.timetrackerapp.data

import com.example.timetrackerapp.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeSlicesRepository : SlicesRepository {
    // "fake" in-memory implementation
    private var runningSliceStateFlow = MutableStateFlow<RunningSlice?>(null)

    private var finishedSlicesStateFlow = MutableStateFlow(listOf<WorkSlice>())

    private val mutex = Mutex()

    private fun findSliceById(id: Int?) = finishedSlicesStateFlow.value.find { it.id == id }

    override fun observeFinishedSlices(): Flow<List<WorkSlice>> {
        return finishedSlicesStateFlow
    }

    override suspend fun getFinishedSlice(id: Int): Result<WorkSlice?> {
        return Result.success(findSliceById(id))
    }

    override suspend fun updateFinishedSlice(id: Int, sliceChanges: SliceChanges) {
        mutex.withLock {
            val oldSlice = findSliceById(id) ?: return
            finishedSlicesStateFlow.update { oldSlicesState ->
                val newSlice = oldSlice.applyChanges(sliceChanges)
                (oldSlicesState - oldSlice + newSlice).sortedBy { it.startInstant }
            }
        }
    }

    override fun observeRunningSlice(): Flow<RunningSlice?> {
        return runningSliceStateFlow
    }

    override suspend fun startRunningSlice(description: Description, task: Task, project: Project) {
        runningSliceStateFlow.value = RunningSlice(
            id = 0,
            project,
            task,
            description,
            isPaused = false,
            listOf(OpenTimeInterval())
        )
    }

    override suspend fun updateRunningSlice(sliceChanges: SliceChanges) {
        mutex.withLock {
            runningSliceStateFlow.update { slice ->
                slice?.applyChanges(sliceChanges)
            }
        }
    }

    override suspend fun changeRunningSliceState(state: WorkSlice.State) {
        if (state == WorkSlice.State.FINISHED) return finishRunningSlice()
        mutex.withLock {
            runningSliceStateFlow.update { slice ->
                if (state == WorkSlice.State.PAUSED)
                    slice?.pause()
                else
                    slice?.resume()
            }
        }
    }

    override suspend fun finishRunningSlice() {
        mutex.withLock {
            runningSliceStateFlow.value?.let { slice ->
                val nextId = (finishedSlicesStateFlow.value.maxOfOrNull { it.id } ?: 0) + 1
                finishedSlicesStateFlow.update { list ->
                    list + WorkSlice(
                        id = nextId,
                        project = slice.project,
                        task = slice.task,
                        description = slice.description,
                        startInstant = slice.startInstant,
                        finishInstant = slice.countCurrentFinishInstant(),
                        duration = slice.countCurrentDuration(),
                        state = WorkSlice.State.FINISHED,
                    )
                }
            }
            runningSliceStateFlow.update { null }
        }
    }
}