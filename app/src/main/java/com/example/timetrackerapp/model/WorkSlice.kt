package com.example.timetrackerapp.model

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.format.TextStyle
import java.util.*
import kotlin.time.Duration

data class WorkSlice(
    val id: Int,
    val project: Project,
    val task: Task,
    val description: Description,
    val state: State,
    val intervals: List<TimeInterval>,
) {
    enum class State {
        RUNNING, PAUSED, FINISHED
    }

    init {
        check(intervals.isNotEmpty())
    }

    val startTime: LocalDateTime
        get() = intervals.startTime()

    val finishTime: LocalDateTime
        get() = intervals.last().finishTime

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

    fun copy(newStartTime: LocalDateTime): TimeInterval
}

data class ClosedTimeInterval(
    override val startInstant: Instant,
    override val duration: Duration,
) : TimeInterval {

    override val isFinished: Boolean
        get() = true

    override fun finish() = this

    override fun copy(newStartTime: LocalDateTime) =
        copy(startInstant = newStartTime.toInstant(TimeZone.currentSystemDefault()))
}

data class OpenTimeInterval(
    override val startInstant: Instant = Clock.System.now()
) : TimeInterval {

    override val duration: Duration
        get() = Clock.System.now() - startInstant

    override val isFinished: Boolean
        get() = false

    override fun finish() = ClosedTimeInterval(startInstant, duration)

    override fun copy(newStartTime: LocalDateTime) =
        copy(startInstant = newStartTime.toInstant(TimeZone.currentSystemDefault()))
}

fun List<TimeInterval>.startTime() = first().startTime

fun List<TimeInterval>.closeLast(): List<TimeInterval> =
    if (isEmpty() || last() is ClosedTimeInterval) this
    else dropLast(1) + last().finish()

fun List<TimeInterval>.countDuration() =
    fold(Duration.ZERO) { sum, interval ->
        sum + interval.duration
    }

private fun renderTimeComponents(hours: Long, minutes: Int, seconds: Int? = null) =
    "${renderTimeComponent(hours)}:${renderTimeComponent(minutes)}" +
            if (seconds != null) ":${renderTimeComponent(seconds)}" else ""

private fun renderTimeComponent(timeComponent: Number) = "%02d".format(timeComponent)

fun LocalDateTime.renderDate(): String =
    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) +
            ", $dayOfMonth " +
            month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

fun LocalDate.render(): String =
    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) +
            ", $dayOfMonth " +
            month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

fun LocalDateTime.renderTime(): String =
    renderTimeComponents(hour.toLong(), minute)

fun Duration.renderDuration(state: WorkSlice.State): String =
    if (state == WorkSlice.State.FINISHED)
        renderDurationFinished()
    else
        renderDurationLive()

fun Duration.renderDurationLive(): String =
    toComponents { hours, minutes, seconds, _ ->
        renderTimeComponents(hours, minutes, seconds)
    }

fun Duration.renderDurationFinished(): String =
    toComponents { hours, minutes, seconds, _ ->
        if (hours == 0L && minutes == 0) "${seconds}s"
        else if (hours == 0L) "${minutes}m"
        else "${hours}h ${minutes}m"
    }
