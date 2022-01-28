package com.example.timetrackerapp.model

import kotlinx.datetime.*
import kotlin.time.Duration

data class BlockOfWork(
    val project: Project,
    val task: Task,
    val description: Description,
    val state: State,
    val intervals: List<TimeInterval>,
) {
    enum class State {
        CREATED, RUNNING, PAUSED, FINISHED
    }

    private fun List<TimeInterval>.checkNonEmpty() = this.also {
        if (isEmpty()) throw IllegalStateException("Current time block is not started")
    }

    val startTime: LocalDateTime
        get() = intervals.checkNonEmpty().startTime()

    val finishTime: LocalDateTime
        get() = intervals.checkNonEmpty().last().finishTime

    val duration: Duration
        get() = intervals.countDuration()
}

data class Project(val value: String)

data class Task(val value: String)

data class Description(val value: String)

sealed interface TimeInterval {
    val startInstant: Instant

    val duration: Duration

    val isFinished: Boolean

    val startTime: LocalDateTime
        get() = startInstant.toLocalDateTime(TimeZone.currentSystemDefault())

    val finishTime: LocalDateTime
        get() = (startInstant + duration).toLocalDateTime(TimeZone.currentSystemDefault())

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

fun List<TimeInterval>.startTime() = first().startTime

fun List<TimeInterval>.closeLast(): List<TimeInterval> =
    if (isEmpty() || last() is ClosedTimeInterval) this
    else dropLast(1) + last().finish()

fun List<TimeInterval>.countDuration() =
    fold(Duration.ZERO) { sum, interval ->
        sum + interval.duration
    }

private fun renderTimeComponents(hours: Int, minutes: Int, seconds: Int? = null) =
    "${renderTimeComponent(hours)}:${renderTimeComponent(minutes)}" +
            if (seconds != null) ":${renderTimeComponent(seconds)}" else ""

private fun renderTimeComponent(timeComponent: Int) = "%02d".format(timeComponent)

fun LocalDateTime.renderTime(): String =
    renderTimeComponents(hour, minute)

fun Duration.renderDuration(): String =
    toComponents { hours, minutes, seconds, _ ->
        renderTimeComponents(hours, minutes, seconds)
    }
