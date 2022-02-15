package com.example.timetrackerapp.data

import com.example.timetrackerapp.model.FinishedSlice
import com.example.timetrackerapp.model.RunningSlice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeSlicesRepository : SlicesRepository {
    // "fake" in-memory implementation
    private var runningSliceStateFlow = MutableStateFlow<RunningSlice?>(null)

    private var finishedSlicesStateFlow = MutableStateFlow(listOf<FinishedSlice>())

    private val mutex = Mutex()

    private fun findSliceById(id: Int?) = finishedSlicesStateFlow.value.find { it.id == id }

    override fun observeFinishedSlices(): Flow<List<FinishedSlice>> {
        return finishedSlicesStateFlow
    }

    override suspend fun getFinishedSlice(id: Int): Result<FinishedSlice?> {
        return Result.success(findSliceById(id))
    }

    override suspend fun updateFinishedSlice(slice: FinishedSlice) {
        mutex.withLock {
            val oldSlice = findSliceById(slice.id)
            finishedSlicesStateFlow.update { oldSlicesState ->
                if (oldSlice != null) {
                    (oldSlicesState - oldSlice + slice).sortedBy { it.startTime }
                } else {
                    oldSlicesState + slice
                }
            }
        }
    }

    override fun observeRunningSlice(): Flow<RunningSlice?> {
        return runningSliceStateFlow
    }

    override suspend fun updateRunningSlice(slice: RunningSlice) {
        mutex.withLock {
            runningSliceStateFlow.value = slice
        }
    }

    override suspend fun finishRunningSlice() {
        mutex.withLock {
            runningSliceStateFlow.value?.let { slice ->
                val nextId = (finishedSlicesStateFlow.value.maxOfOrNull { it.id } ?: 0) + 1
                finishedSlicesStateFlow.update { list ->
                    list + FinishedSlice(
                        id = nextId,
                        project = slice.project,
                        task = slice.task,
                        description = slice.description,
                        startInstant = slice.startInstant,
                        finishInstant = slice.finishInstant,
                        duration = slice.duration
                    )
                }
            }
        }
    }
}