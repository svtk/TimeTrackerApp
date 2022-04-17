package com.github.jetbrains.timetracker.androidapp.data

import com.github.jetbrains.timetracker.androidapp.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class FakeSlicesRepository(
    fakeData: FakeData
) : SlicesRepository {
    // "fake" in-memory implementation
    private var runningSliceStateFlow = MutableStateFlow<RunningSlice?>(null)

//    private var finishedSlices by mutableStateOf(FakeData.randomSlices)

    private var finishedSlicesStateFlow = MutableStateFlow(fakeData.randomSlices)

    private val mutex = Mutex()

    private fun findSliceById(id: UUID?) = finishedSlicesStateFlow.value.find { it.id == id }

    override suspend fun getTimeRanges(): List<TimeRange> {
        val finishedSlices = finishedSlicesStateFlow.value
        if (finishedSlices.isEmpty()) return emptyList()

        val first = finishedSlices.minOf { it.startInstant }
        val last = finishedSlices.maxOf { it.startInstant }
        return createWeeksRanges(first, last)
    }

    override fun observeFinishedSlices(timeRange: TimeRange): Flow<WorkSlicesByDays> {
        return finishedSlicesStateFlow
            .map { list ->
                list
                    .filter { slice -> slice.startInstant in timeRange }
                    .toWorkSlicesByDays()
            }
    }

    override fun getFinishedSlices(timeRange: TimeRange): Result<WorkSlicesByDays> {
        return Result.success(
            finishedSlicesStateFlow.value
                .filter { it.startInstant in timeRange }
                .toWorkSlicesByDays()
        )
    }

//    override fun observeFinishedSlices(): Flow<List<WorkSlice>> {
//        return finishedSlicesStateFlow
//    }

    override fun observeWorkActivitiesSuggestions(): Flow<WorkActivitySuggestions> {
        return finishedSlicesStateFlow.map { it.buildActivitySuggestions() }
    }

    override suspend fun getFinishedSlice(id: UUID): Result<WorkSlice?> {
        return Result.success(findSliceById(id))
    }

    override suspend fun updateFinishedSlice(id: UUID, sliceChanges: SliceChanges) {
        mutex.withLock {
            val oldSlice = findSliceById(id) ?: return
            finishedSlicesStateFlow.update { oldSlicesState ->
                val newSlice = oldSlice.applyChanges(sliceChanges)
                oldSlicesState - oldSlice + newSlice
            }
        }
    }

    override fun observeRunningSlice(): Flow<RunningSlice?> {
        return runningSliceStateFlow
    }

    override suspend fun startRunningSlice(
        description: Description,
        task: Task?,
        project: Project?
    ) {
        runningSliceStateFlow.value = RunningSlice(
            activity = WorkActivity(
                project = project,
                task = task,
                description = description,
            )
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
                finishedSlicesStateFlow.update { list ->
                    list + slice.finish()
                }
            }
            runningSliceStateFlow.update { null }
        }
    }
}