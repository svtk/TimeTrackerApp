package com.example.timetrackerapp.data

import com.example.timetrackerapp.model.FinishedSlice
import com.example.timetrackerapp.model.RunningSlice
import kotlinx.coroutines.flow.Flow

interface SlicesRepository {

    fun observeFinishedSlices(): Flow<List<FinishedSlice>>

    suspend fun getFinishedSlice(id: Int): Result<FinishedSlice?>

    suspend fun updateFinishedSlice(slice: FinishedSlice)

    fun observeRunningSlice(): Flow<RunningSlice?>

    suspend fun updateRunningSlice(slice: RunningSlice)

    suspend fun finishRunningSlice()
}