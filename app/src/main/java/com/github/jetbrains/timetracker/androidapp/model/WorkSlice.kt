package com.github.jetbrains.timetracker.androidapp.model

import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration

data class WorkSlice(
    val id: UUID,
    val project: Project,
    val task: Task,
    val description: Description,
    val startInstant: Instant,
    val finishInstant: Instant,
    val duration: Duration,
    val state: State,
) {
    enum class State {
        RUNNING, PAUSED, FINISHED
    }
}

data class Project(val value: String)

data class Task(val value: String)

data class Description(val value: String)