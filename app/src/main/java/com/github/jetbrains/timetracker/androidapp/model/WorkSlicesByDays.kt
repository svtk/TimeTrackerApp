package com.github.jetbrains.timetracker.androidapp.model

data class WorkSlicesByDays(
    val entries: Map<TimeRange, List<WorkSlice>> = emptyMap()
)

fun List<WorkSlice>.toWorkSlicesByDays() =
    WorkSlicesByDays(
        this
            .groupBy { createDayRange(it.startInstant) }
            .mapValues { (_, list) -> list.sortedBy(WorkSlice::startInstant) }
    )
