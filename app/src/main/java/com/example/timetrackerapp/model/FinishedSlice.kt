package com.example.timetrackerapp.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

interface WorkSlice {

    val id: Int

    val project: Project

    val task: Task

    val description: Description

    val state: State

    val startInstant: Instant

    val finishInstant: Instant

    val duration: Duration

    enum class State {
        RUNNING, PAUSED, FINISHED
    }

    val startTime: LocalDateTime
        get() = startInstant.toLocalDateTime(TimeZone.currentSystemDefault())

    val finishTime: LocalDateTime
        get() = finishInstant.toLocalDateTime(TimeZone.currentSystemDefault())
}

data class FinishedSlice(
    override val id: Int,
    override val project: Project,
    override val task: Task,
    override val description: Description,
    override val startInstant: Instant,
    override val finishInstant: Instant,
    override val duration: Duration
) : WorkSlice {
    override val state: WorkSlice.State
        get() = WorkSlice.State.FINISHED
}

data class Project(val value: String)

data class Task(val value: String)

data class Description(val value: String)
