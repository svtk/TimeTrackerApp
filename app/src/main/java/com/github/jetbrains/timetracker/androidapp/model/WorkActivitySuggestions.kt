package com.github.jetbrains.timetracker.androidapp.model

import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

data class WorkActivitySuggestions(
    val lastCompleted: List<WorkActivity> = emptyList(),
    val suggestions: List<WorkActivity> = emptyList(),
)

private val RECENT_INTERVAL = 1.minutes

fun List<WorkSlice>.buildActivitySuggestions(): WorkActivitySuggestions {
    if (this.isEmpty()) return WorkActivitySuggestions()

    val withLatestFinishInstants = this.groupBy { it.activity }
        .map { (activity, slices) -> Pair(activity, slices.maxOf { it.finishInstant }) }
    val sorted = withLatestFinishInstants.sortedByDescending { it.second }
    val theMostRecent = sorted.first()

    val (lastCompleted, suggestions) =
        if (Clock.System.now() - theMostRecent.second < RECENT_INTERVAL) {
            Pair(listOf(theMostRecent), sorted.drop(1))
        } else {
            Pair(emptyList(), sorted)
        }
    return WorkActivitySuggestions(
        lastCompleted.map { it.first },
        suggestions.map { it.first }
    )
}