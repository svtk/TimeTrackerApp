package com.example.timetrackerapp.util

import com.example.timetrackerapp.model.ClosedTimeInterval
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun testTimeIntervals() = listOf(
    testTimeInterval("2022-01-26T11:30", 20.minutes),
    testTimeInterval("2022-01-26T13:00", 30.minutes),
)

fun testTimeInterval(isoDate: String, duration: Duration) =
    ClosedTimeInterval(testInstant(isoDate), duration)

fun testInstant(isoDate: String) = LocalDateTime.parse(isoDate).toInstant(
    TimeZone.currentSystemDefault()
)