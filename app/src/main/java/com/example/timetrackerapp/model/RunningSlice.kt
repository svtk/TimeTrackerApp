package com.example.timetrackerapp.model

import kotlinx.datetime.*
import kotlin.time.Duration

data class RunningSlice(
    override val id: Int,
    override val project: Project,
    override val task: Task,
    override val description: Description,
    override val state: WorkSlice.State,
    val intervals: List<TimeInterval>,
) : WorkSlice {

    init {
        check(intervals.isNotEmpty())
    }

    override val startInstant: Instant
        get() = intervals.first().startInstant

    override val finishInstant: Instant
        get() = intervals.last().startInstant + duration

    override val duration: Duration
        get() = intervals.countDuration()
}

sealed interface TimeInterval {
    val startInstant: Instant

    val duration: Duration

    val isFinished: Boolean

    fun finish(): TimeInterval
}

data class ClosedTimeInterval(
    override val startInstant: Instant,
    override val duration: Duration,
) : TimeInterval {

    override val isFinished: Boolean
        get() = true

    override fun finish() = this
}

data class OpenTimeInterval(
    override val startInstant: Instant = Clock.System.now()
) : TimeInterval {

    override val duration: Duration
        get() = Clock.System.now() - startInstant

    override val isFinished: Boolean
        get() = false

    override fun finish() = ClosedTimeInterval(startInstant, duration)
}

fun List<TimeInterval>.closeLast(): List<TimeInterval> =
    if (isEmpty() || last() is ClosedTimeInterval) this
    else dropLast(1) + last().finish()

fun List<TimeInterval>.countDuration() =
    fold(Duration.ZERO) { sum, interval ->
        sum + interval.duration
    }

fun List<TimeInterval>.replaceStartTime(newStartTime: LocalDateTime): List<TimeInterval> =
    map {
        val startInstant = newStartTime.toInstant(TimeZone.currentSystemDefault())
        when (it) {
            is ClosedTimeInterval -> it.copy(startInstant = startInstant)
            is OpenTimeInterval -> it.copy(startInstant = startInstant)
        }
    }