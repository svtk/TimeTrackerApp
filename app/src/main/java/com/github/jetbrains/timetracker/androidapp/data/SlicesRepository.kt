package com.github.jetbrains.timetracker.androidapp.data

import com.github.jetbrains.timetracker.androidapp.model.*
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import java.util.*

interface SlicesRepository : KoinComponent {

    fun observeFinishedSlices(): Flow<List<WorkSlice>>

    suspend fun getFinishedSlice(id: UUID): Result<WorkSlice?>

    suspend fun updateFinishedSlice(id: UUID, sliceChanges: SliceChanges)

    fun observeRunningSlice(): Flow<RunningSlice?>

    suspend fun startRunningSlice(description: Description, task: Task, project: Project)

    suspend fun updateRunningSlice(sliceChanges: SliceChanges)

    suspend fun changeRunningSliceState(state: WorkSlice.State)

    suspend fun finishRunningSlice()
}