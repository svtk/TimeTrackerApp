package com.example.timetrackerapp.model

import com.example.timetrackerapp.util.applyDateAndTimeChanges
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

data class SliceChanges(
    val newProject: Project? = null,
    val newTask: Task? = null,
    val newDescription: Description? = null,
    val newStartDate: LocalDate? = null,
    val newStartTime: LocalDateTime? = null,
    val newFinishDate: LocalDate? = null,
    val newFinishTime: LocalDateTime? = null,
    val newDuration: Duration? = null,
)

fun WorkSlice.applyChanges(sliceChanges: SliceChanges?): WorkSlice {
    if (sliceChanges == null) return this
    val newStartInstant = startInstant.applyDateAndTimeChanges(
        sliceChanges.newStartDate, sliceChanges.newStartTime
    )
    val newFinishInstant = finishInstant.applyDateAndTimeChanges(
        sliceChanges.newFinishDate, sliceChanges.newFinishTime
    )
    return copy(
        project = sliceChanges.newProject ?: project,
        task = sliceChanges.newTask ?: task,
        description = sliceChanges.newDescription ?: description,
        startInstant = newStartInstant,
        finishInstant = newFinishInstant,
        duration = sliceChanges.newDuration ?: duration,
    )
}

fun RunningSlice.applyChanges(sliceChanges: SliceChanges?): RunningSlice {
    if (sliceChanges == null) return this
    val newStartInstant = startInstant.applyDateAndTimeChanges(
        sliceChanges.newStartDate, sliceChanges.newStartTime
    )
    return copy(
        project = sliceChanges.newProject ?: project,
        task = sliceChanges.newTask ?: task,
        description = sliceChanges.newDescription ?: description,
        intervals = if (newStartInstant != startInstant) {
            val initialIntervals = intervals.filter { !it.isArtificial }
            listOf(
                ClosedTimeInterval(
                    startInstant = newStartInstant,
                    finishInstant = initialIntervals.first().startInstant,
                    isArtificial = true,
                )
            ) + initialIntervals
        } else intervals
    )
}