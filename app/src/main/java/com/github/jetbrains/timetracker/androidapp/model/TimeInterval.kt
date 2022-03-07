package com.github.jetbrains.timetracker.androidapp.model

import kotlinx.datetime.*
import kotlin.time.Duration

sealed interface TimeInterval {
    val startInstant: Instant

    val duration: Duration

    val isFinished: Boolean

    val isArtificial: Boolean

    fun finish(): TimeInterval
}

data class ClosedTimeInterval(
    override val startInstant: Instant,
    override val duration: Duration,
    override val isArtificial: Boolean = false,
) : TimeInterval {
    constructor(startInstant: Instant, finishInstant: Instant, isArtificial: Boolean = false):
            this(startInstant, duration = finishInstant - startInstant, isArtificial = isArtificial)

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

    override val isArtificial: Boolean
        get() = false

    override fun finish() =
        ClosedTimeInterval(
            startInstant = startInstant,
            finishInstant = Clock.System.now()
        )
}

fun List<TimeInterval>.closeLast(): List<TimeInterval> =
    if (isEmpty() || last() is ClosedTimeInterval) this
    else dropLast(1) + last().finish()

fun List<TimeInterval>.countDuration() =
    fold(Duration.ZERO) { sum, interval ->
        sum + interval.duration
    }