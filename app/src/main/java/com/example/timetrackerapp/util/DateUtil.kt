package com.example.timetrackerapp.util

import kotlinx.datetime.*

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