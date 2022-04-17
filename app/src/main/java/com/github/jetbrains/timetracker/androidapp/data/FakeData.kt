package com.github.jetbrains.timetracker.androidapp.data

import com.github.jetbrains.timetracker.androidapp.model.*
import com.github.jetbrains.timetracker.androidapp.util.copy
import kotlinx.datetime.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object FakeData {
    private val random = Random(1)
    private fun <T> List<T>.random() = this[random.nextInt(this.size)]

    private val projects =
        listOf("app", "blog", "p3", "p4", "p5", "p6")
            .map { Project("$it project") }

    private val tasks =
        ('A'..'L').map { Task("$it$it") }

    private val descriptions = listOf(
        "coding",
        "reading",
        "learning",
        "doing something",
        "doing something else",
        "doing something very important",
        "playing",
        "researching",
        "reading a book",
        "reading an article",
        "reading slack",
        "procrastinating",
        "looking for a problem",
        "writing code",
        "reading code",
        "fixing bug",
        "being in a meeting",
        "talking",
        "chatting",
        "preparing slides",
        "eating chocolate",
        "working, simply working",
        "tweeting",
        "writing a blogpost",
        "doing nothing",
        "thinking",
        "setting up an environment",
        "fixing an infrastructural issue",
        "logging status",
        "sleeping",
        "criticizing other's work",
        "having arguments with colleagues",
    )
        .map { Description(it) }

    private val activities = buildList {
        (0..1).forEach { add(WorkActivity(projects[0], tasks[0], descriptions[it])) }
        (2..4).forEach { add(WorkActivity(projects[0], tasks[1], descriptions[it])) }
        (5..6).forEach { add(WorkActivity(projects[1], tasks[2], descriptions[it])) }
        (7..9).forEach { add(WorkActivity(projects[1], tasks[3], descriptions[it])) }
        (10..11).forEach { add(WorkActivity(projects[2], tasks[4], descriptions[it])) }
        (12..14).forEach { add(WorkActivity(projects[2], tasks[5], descriptions[it])) }
        (15..16).forEach { add(WorkActivity(projects[2], tasks[6], descriptions[it])) }
        (17..18).forEach { add(WorkActivity(projects[3], tasks[7], descriptions[it])) }
        (19..21).forEach { add(WorkActivity(projects[3], tasks[8], descriptions[it])) }
        (22..23).forEach { add(WorkActivity(projects[4], tasks[9], descriptions[it])) }
        (24..26).forEach { add(WorkActivity(projects[5], tasks[10], descriptions[it])) }
        (27..30).forEach { add(WorkActivity(projects[5], tasks[11], descriptions[it])) }
    }

    private fun randomDayActivities(start: Instant): List<WorkSlice> = buildList {
        var current = start + Random.nextInt(30).minutes
        while (current < start + 8.hours) {
            val duration = Random.nextInt(120).minutes
            add(WorkSlice(
                activity = activities.random(),
                startInstant = current,
                finishInstant = current + duration,
                duration = duration,
                state = WorkSlice.State.FINISHED,
            ))
            current += (duration + Random.nextInt(30).minutes)
        }
    }

    val randomSlices = buildList {
        val now = Clock.System.now()
        val defaultTimeZone = TimeZone.currentSystemDefault()
        val start = (now - 60.days).toLocalDateTime(defaultTimeZone).copy(
            hour = 8,
            minute = 0,
            second = 0,
            nanosecond = 0,
        ).toInstant(defaultTimeZone)
        (0..59).forEach {
            val day = start + it.days
            addAll(randomDayActivities(day))
        }
    }
}