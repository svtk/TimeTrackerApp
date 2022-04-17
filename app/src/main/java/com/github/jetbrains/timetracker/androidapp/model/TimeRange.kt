package com.github.jetbrains.timetracker.androidapp.model

import com.github.jetbrains.timetracker.androidapp.util.*
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days

data class TimeRange(
    val range: ClosedRange<Instant>,
    val title: String
) : ClosedRange<Instant> by range

fun createRangeForToday(): TimeRange =
    createDayRange(Clock.System.now())

fun createDayRange(instant: Instant): TimeRange {
    val day = instant.toLocalDateTime(tz)
    val start = startOfTheDay(day)
    val end = endOfTheDay(day)
    return TimeRange(start..end, start.dateFullLabel())
}

fun createDaysRange(from: Instant, to: Instant): TimeRange {
    val start = startOfTheDay(from.toLocalDateTime(tz))
    val end = endOfTheDay(to.toLocalDateTime(tz))
    return TimeRange(start..end,
        "${from.dateShortLabel()} - ${to.dateShortLabel()}")
}

fun createWeeksRanges(from: Instant, to: Instant): List<TimeRange> = buildList {
    val firstMonday = from.closestBefore(WEEK_START)
    val lastFriday = to.closestAfter(WEEK_END)
    var current = firstMonday
    while (current < lastFriday) {
        add(createDaysRange(current, current + 6.days))
        current += 7.days
    }
}

private fun startOfTheDay(day: LocalDateTime): Instant {
    return LocalDateTime(day.year, day.month, day.dayOfMonth, 0, 0, 0)
        .toInstant(tz)
}

private fun endOfTheDay(day: LocalDateTime) =
    LocalDateTime(day.year, day.month, day.dayOfMonth, 23, 59, 59)
        .toInstant(tz)

private fun Instant.closestBefore(dayOfWeek: DayOfWeek): Instant {
    return closestDate(dayOfWeek, forward = false)
}

private fun Instant.closestAfter(dayOfWeek: DayOfWeek): Instant {
    return closestDate(dayOfWeek, forward = true)
}

private fun Instant.closestDate(dayOfWeek: DayOfWeek, forward: Boolean): Instant {
    val tz = TimeZone.currentSystemDefault()
    val startDate = this.toLocalDateTime(tz)
    if (startDate.dayOfWeek == dayOfWeek) return this
    return (1..6)
        .asSequence()
        .map { i -> if (forward) this + i.days else this - i.days }
        .first { instant -> instant.toLocalDateTime(tz).dayOfWeek == dayOfWeek }
}


