package com.example.timetrackerapp.data

import com.example.timetrackerapp.model.WorkSlice
import kotlinx.coroutines.flow.Flow

interface SlicesRepository {

    fun observeFinishedSlices(): Flow<List<WorkSlice>>

    suspend fun getFinishedSlice(id: Int): Result<WorkSlice?>

    suspend fun updateFinishedSlice(slice: WorkSlice)

    fun observeRunningSlice(): Flow<WorkSlice?>

    suspend fun updateRunningSlice(slice: WorkSlice)

    suspend fun finishRunningSlice()
}