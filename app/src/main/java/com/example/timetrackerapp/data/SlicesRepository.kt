package com.example.timetrackerapp.data

import com.example.timetrackerapp.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

interface SlicesRepository {

    fun observeFinishedSlices(): Flow<List<WorkSlice>>

    suspend fun getFinishedSlice(id: UUID): Result<WorkSlice?>

    suspend fun updateFinishedSlice(id: UUID, sliceChanges: SliceChanges)

    fun observeRunningSlice(): Flow<RunningSlice?>

    suspend fun startRunningSlice(description: Description, task: Task, project: Project)

    suspend fun updateRunningSlice(sliceChanges: SliceChanges)

    suspend fun changeRunningSliceState(state: WorkSlice.State)

    suspend fun finishRunningSlice()
}