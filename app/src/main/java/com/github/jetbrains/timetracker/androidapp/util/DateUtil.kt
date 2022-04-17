package com.github.jetbrains.timetracker.androidapp.util

import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days

val WEEK_START = DayOfWeek.MONDAY
val WEEK_END = DayOfWeek.SUNDAY

val tz = TimeZone.currentSystemDefault()

fun LocalDateTime.copy(
    year: Int = this.year,
    monthNumber: Int = this.monthNumber,
    dayOfMonth: Int = this.dayOfMonth,
    hour: Int = this.hour,
    minute: Int = this.minute,
    second: Int = this.second,
    nanosecond: Int = this.nanosecond
) = LocalDateTime(
    year = year,
    monthNumber = monthNumber,
    dayOfMonth = dayOfMonth,
    hour = hour,
    minute = minute,
    second = second,
    nanosecond = nanosecond
)

fun Instant.dateFullLabel(): String {
    val day = toLocalDateTime(tz)
    val today = Clock.System.now().toLocalDateTime(tz)
    val yesterday = (Clock.System.now() - 1.days).toLocalDateTime(tz)
    return when (day.date) {
        today.date -> "today"
        yesterday.date -> "yesterday"
        else -> "${day.dayOfWeek} ${day.month} ${day.dayOfMonth}"
    }
}

fun Instant.dateShortLabel(): String {
    val day = toLocalDateTime(tz)
    val month = day.month.toString().take(3)
    return "$month ${day.dayOfMonth}"
}

fun Instant.applyDateAndTimeChanges(
    newStartDate: LocalDate?,
    newStartTime: LocalDateTime?
): Instant {
    if (newStartDate == null && newStartTime == null) return this
    return toLocalDateTime(TimeZone.currentSystemDefault())
        .let { time -> if (newStartDate != null) time.replaceDate(newStartDate) else time }
        .let { time -> if (newStartTime != null) time.replaceTime(newStartTime) else time }
        .toInstant(TimeZone.currentSystemDefault())
}

private fun LocalDateTime.replaceDate(
    newDate: LocalDate
) = LocalDateTime(
    year = newDate.year,
    month = newDate.month,
    dayOfMonth = newDate.dayOfMonth,
    hour = hour,
    minute = minute,
    second = second,
    nanosecond = nanosecond
)

private fun LocalDateTime.replaceTime(
    newTime: LocalDateTime
) = LocalDateTime(
    year = year,
    month = month,
    dayOfMonth = dayOfMonth,
    hour = newTime.hour,
    minute = newTime.minute,
    second = newTime.second,
    nanosecond = newTime.nanosecond
)