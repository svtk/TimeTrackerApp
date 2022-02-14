package com.example.timetrackerapp.data

import com.example.timetrackerapp.model.WorkSlice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeSlicesRepository : SlicesRepository {
    // "fake" in-memory implementation
    private var runningSliceStateFlow = MutableStateFlow<WorkSlice?>(null)

    private var finishedSlicesStateFlow = MutableStateFlow(listOf<WorkSlice>())

    private val mutex = Mutex()

    private fun findSliceById(id: Int?) = finishedSlicesStateFlow.value.find { it.id == id }

    override fun observeFinishedSlices(): Flow<List<WorkSlice>> {
        return finishedSlicesStateFlow
    }

    override suspend fun getFinishedSlice(id: Int): Result<WorkSlice?> {
        return Result.success(findSliceById(id))
    }

    override suspend fun updateFinishedSlice(slice: WorkSlice) {
        mutex.withLock {
            val oldSlice = findSliceById(slice.id)
            val oldSlicesState = finishedSlicesStateFlow.value
            finishedSlicesStateFlow.value = if (oldSlice != null) {
                (oldSlicesState - oldSlice + slice).sortedBy { it.startTime }
            } else {
                oldSlicesState + slice
            }
        }
    }

    override fun observeRunningSlice(): Flow<WorkSlice?> {
        return runningSliceStateFlow
    }

    override suspend fun updateRunningSlice(slice: WorkSlice) {
        mutex.withLock {
            runningSliceStateFlow.value = slice
        }
    }

    override suspend fun finishRunningSlice() {
        mutex.withLock {
            val newFinishedSlice = runningSliceStateFlow.value
            if (newFinishedSlice != null) {
                val nextId = (finishedSlicesStateFlow.value.maxOfOrNull { it.id } ?: 0) + 1
                finishedSlicesStateFlow.value =
                    finishedSlicesStateFlow.value + newFinishedSlice.copy(id = nextId)
            }
        }
    }
}