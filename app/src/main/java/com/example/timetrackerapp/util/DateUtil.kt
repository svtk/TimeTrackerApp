package com.example.timetrackerapp.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

fun LocalDateTime.replaceDate(
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