package com.github.jetbrains.timetracker.androidapp.data.firestore

import com.github.jetbrains.timetracker.androidapp.model.Description
import com.github.jetbrains.timetracker.androidapp.model.Project
import com.github.jetbrains.timetracker.androidapp.model.Task
import com.github.jetbrains.timetracker.androidapp.model.WorkSlice
import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

data class FirestoreWorkSlice(
    val id: String? = null,
    val project: String? = null,
    val task: String? = null,
    val description: String? = null,
    val startInstantMillis: Long? = null,
    val finishInstantMillis: Long? = null,
    val durationMillis: Long? = null,
    val state: String? = null,
)

fun WorkSlice.toFirestoreWorkSlice() = FirestoreWorkSlice(
    id = id.toString(),
    project = project.value,
    task = task.value,
    description = description.value,
    startInstantMillis = startInstant.toEpochMilliseconds(),
    finishInstantMillis = finishInstant.toEpochMilliseconds(),
    durationMillis = duration.inWholeMilliseconds,
    state = state.name,
)

fun FirestoreWorkSlice.toWorkSlice(): WorkSlice? {
    if (id == null) return null
    return WorkSlice(
        id = UUID.fromString(id),
        project = Project(project!!),
        task = Task(task!!),
        description = Description(description!!),
        startInstant = Instant.fromEpochMilliseconds(startInstantMillis!!),
        finishInstant = Instant.fromEpochMilliseconds(finishInstantMillis!!),
        duration = durationMillis!!.milliseconds,
        state = WorkSlice.State.valueOf(state!!),
    )
}