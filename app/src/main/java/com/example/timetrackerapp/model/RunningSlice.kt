package com.example.timetrackerapp.model

import kotlinx.datetime.*
import kotlin.time.Duration

data class RunningSlice(
    val id: Int,
    val project: Project,
    val task: Task,
    val description: Description,
    val isPaused: Boolean,
    val intervals: List<TimeInterval>,
) {
    init {
        check(intervals.isNotEmpty())
    }

    val startInstant: Instant
        get() = intervals.minOf { it.startInstant }

    fun countCurrentFinishInstant(): Instant =
        startInstant + countCurrentDuration()

    fun countCurrentDuration(): Duration =
        intervals.countDuration()
}

fun RunningSlice.pause(): RunningSlice =
    if (isPaused) this else copy(
        isPaused = true,
        intervals = intervals.closeLast(),
    )

fun RunningSlice.resume(): RunningSlice =
    if (!isPaused) this else copy(
        isPaused = false,
        intervals = intervals + OpenTimeInterval(),
    )
