package com.example.timetrackerapp.util

import com.example.timetrackerapp.model.WorkSlice
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.time.format.TextStyle
import java.util.*
import kotlin.math.absoluteValue
import kotlin.time.Duration

private fun renderTimeComponents(hours: Long, minutes: Int, seconds: Int? = null): String {
    val sign = if (hours < 0 || minutes < 0 || (seconds != null && seconds < 0)) "-" else ""
    return sign + renderTimeComponent(hours.absoluteValue) + ":" +
            renderTimeComponent(minutes.absoluteValue) +
            if (seconds != null) ":${renderTimeComponent(seconds.absoluteValue)}" else ""
}

private fun renderTimeComponent(timeComponent: Number) = "%02d".format(timeComponent)

fun LocalDateTime.renderDate(): String =
    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) +
            ", $dayOfMonth " +
            month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

fun LocalDate.render(): String =
    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) +
            ", $dayOfMonth " +
            month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

fun LocalDateTime.renderTime(): String =
    renderTimeComponents(hour.toLong(), minute)

fun Duration.renderDuration(state: WorkSlice.State): String =
    if (state == WorkSlice.State.FINISHED)
        renderDurationFinished()
    else
        renderDurationLive()

fun Duration.renderDurationLive(): String =
    toComponents { hours, minutes, seconds, _ ->
        renderTimeComponents(hours, minutes, seconds)
    }

fun Duration.renderDurationFinished(): String =
    toComponents { hours, minutes, seconds, _ ->
        if (hours == 0L && minutes == 0) "${seconds}s"
        else if (hours == 0L) "${minutes}m"
        else "${hours}h ${minutes}m"
    }
