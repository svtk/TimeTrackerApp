package com.github.jetbrains.timetracker.androidapp.model

import kotlinx.datetime.*
import java.util.*
import kotlin.time.Duration

data class RunningSlice(
    val id: UUID = UUID.randomUUID(),
    val activity: WorkActivity,
    val isPaused: Boolean = false,
    val intervals: List<TimeInterval> = listOf(OpenTimeInterval()),
) {
    init {
        check(intervals.isNotEmpty())
    }

    val startInstant: Instant
        get() = intervals.first().startInstant

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

fun RunningSlice.finish(): WorkSlice = WorkSlice(
    id = id,
    activity = activity,
    startInstant = startInstant,
    finishInstant = countCurrentFinishInstant(),
    duration = countCurrentDuration(),
    state = WorkSlice.State.FINISHED,
)
