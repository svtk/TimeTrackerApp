package com.github.jetbrains.timetracker.androidapp.util

import com.github.jetbrains.timetracker.androidapp.model.Description
import com.github.jetbrains.timetracker.androidapp.model.Project
import com.github.jetbrains.timetracker.androidapp.model.Task
import com.github.jetbrains.timetracker.androidapp.model.WorkSlice
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.util.*
import kotlin.time.Duration.Companion.minutes

fun createTestInstant(isoDate: String) = LocalDateTime.parse(isoDate).toInstant(
    TimeZone.currentSystemDefault()
)

fun createTestSlices() = listOf(
    WorkSlice(
        id = UUID.randomUUID(),
        project = Project("project 1"),
        task = Task("task 1"),
        description = Description("short description"),
        startInstant = createTestInstant("2022-01-05T10:00"),
        finishInstant = createTestInstant("2022-01-05T10:20"),
        duration = 25.minutes,
        state = WorkSlice.State.FINISHED,
    ),
    WorkSlice(
        id = UUID.randomUUID(),
        project = Project("project 2 - it should be a very long title"),
        task = Task("task 2 - also a long title"),
        description = Description("the description is soo long that it doesn't fit in one line"),
        startInstant = createTestInstant("2022-01-26T11:30"),
        finishInstant = createTestInstant("2022-01-26T13:00"),
        duration = 50.minutes,
        state = WorkSlice.State.FINISHED,
    ),
)

fun createTestRunningSlice() = WorkSlice(
    id = UUID.randomUUID(),
    project = Project("project 1"),
    task = Task("task 1"),
    description = Description("short description"),
    startInstant = createTestInstant("2022-01-05T10:00"),
    finishInstant = createTestInstant("2022-01-05T10:20"),
    duration = 25.minutes,
    state = WorkSlice.State.RUNNING,
)