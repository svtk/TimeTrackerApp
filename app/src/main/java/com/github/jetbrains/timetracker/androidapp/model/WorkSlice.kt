package com.github.jetbrains.timetracker.androidapp.model

import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration

data class WorkSlice(
    val id: UUID,
    val activity: WorkActivity,
    val startInstant: Instant,
    val finishInstant: Instant,
    val duration: Duration,
    val state: State,
) {
    enum class State {
        RUNNING, PAUSED, FINISHED
    }
}

data class WorkActivity(
    val project: Project?,
    val task: Task?,
    val description: Description,
)

data class Project(val value: String)

data class Task(val value: String)

data class Description(val value: String)