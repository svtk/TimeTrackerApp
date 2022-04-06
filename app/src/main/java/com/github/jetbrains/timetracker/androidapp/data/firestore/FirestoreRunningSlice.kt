package com.github.jetbrains.timetracker.androidapp.data.firestore

import com.github.jetbrains.timetracker.androidapp.model.*
import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

// TODO Use Firebase-Kotlin-SDK from GitLive with kotlinx.serialization and remove these extra classes

data class FirestoreRunningSlice(
    val id: String? = null,
    val project: String? = null,
    val task: String? = null,
    val description: String? = null,
    @field:JvmField
    val isPaused: Boolean? = null,
    val intervals: List<FirestoreTimeInterval>? = null,
)

data class FirestoreTimeInterval(
    val startInstantMillis: Long? = null,
    val durationMillis: Long? = null,
    @field:JvmField
    val isFinished: Boolean? = null,
    @field:JvmField
    val isArtificial: Boolean? = null,
)

fun RunningSlice.toFirestoreRunningSlice() =
    FirestoreRunningSlice(
        id = id.toString(),
        project = project.value,
        task = task.value,
        description = description.value,
        isPaused = isPaused,
        intervals = intervals.map { it.toFirestoreTimeInterval() }
    )

fun FirestoreRunningSlice.toRunningSlice(): RunningSlice? {
    if (id == null) return null
    return RunningSlice(
        id = UUID.fromString(id),
        project = Project(project!!),
        task = Task(task!!),
        description = Description(description!!),
        isPaused = isPaused!!,
        intervals = intervals!!.map { it.toTimeInterval() },
    )
}


fun TimeInterval.toFirestoreTimeInterval() =
    FirestoreTimeInterval(
        startInstantMillis = startInstant.toEpochMilliseconds(),
        durationMillis = duration.inWholeMilliseconds,
        isFinished = isFinished,
        isArtificial = isArtificial,
    )

fun FirestoreTimeInterval.toTimeInterval() =
    if (isFinished!!) {
        ClosedTimeInterval(
            startInstant = Instant.fromEpochMilliseconds(startInstantMillis!!),
            duration = durationMillis!!.milliseconds,
            isArtificial = isArtificial!!,
        )
    } else {
        OpenTimeInterval(
            startInstant = Instant.fromEpochMilliseconds(startInstantMillis!!),
        )
    }

fun SliceChanges.buildDetailsUpdatesMap() = buildMap<String, Any> {
    if (newProject != null) put("project", newProject.value)
    if (newTask != null) put("task", newTask.value)
    if (newDescription != null) put("description", newDescription.value)
    // TODO: start instant
}

fun RunningSlice.buildStateUpdatesMap() = mapOf(
    "isPaused" to isPaused,
    "intervals" to intervals.map { it.toFirestoreTimeInterval() },
)