package com.github.jetbrains.timetracker.androidapp.data.firestore

import com.github.jetbrains.timetracker.androidapp.model.*
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
    project = activity.project?.value,
    task = activity.task?.value,
    description = activity.description.value,
    startInstantMillis = startInstant.toEpochMilliseconds(),
    finishInstantMillis = finishInstant.toEpochMilliseconds(),
    durationMillis = duration.inWholeMilliseconds,
    state = state.name,
)

fun FirestoreWorkSlice.toWorkSlice(): WorkSlice? {
    if (id == null) return null
    return WorkSlice(
        id = UUID.fromString(id),
        activity = WorkActivity(
            project = project?.let { Project(project)},
            task = task?.let { Task(task) },
            description = Description(description!!),
        ),
        startInstant = Instant.fromEpochMilliseconds(startInstantMillis!!),
        finishInstant = Instant.fromEpochMilliseconds(finishInstantMillis!!),
        duration = durationMillis!!.milliseconds,
        state = WorkSlice.State.valueOf(state!!),
    )
}