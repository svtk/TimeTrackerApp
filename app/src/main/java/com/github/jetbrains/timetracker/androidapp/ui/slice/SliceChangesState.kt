package com.github.jetbrains.timetracker.androidapp.ui.slice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.jetbrains.timetracker.androidapp.model.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

class SliceChangesState {
    var changes: SliceChanges by mutableStateOf(SliceChanges())
        private set

    private inline fun recordChanges(update: SliceChanges.() -> SliceChanges) {
        changes = changes.update()
    }

    fun clearChanges() {
        changes = SliceChanges()
    }

    fun onProjectChanged(projectName: String) {
        recordChanges { copy(newProject = Project(projectName)) }
    }

    fun onTaskChanged(taskName: String) {
        recordChanges { copy(newTask = Task(taskName)) }
    }

    fun onDescriptionChanged(description: String) {
        recordChanges { copy(newDescription = Description(description)) }
    }

    fun onStartDateChanged(newDate: LocalDate) {
        recordChanges { copy(newStartDate = newDate) }
    }

    fun onStartTimeChanged(newTime: LocalDateTime) {
        recordChanges { copy(newStartTime = newTime) }
    }

    fun onFinishDateChanged(newDate: LocalDate) {
        recordChanges { copy(newFinishDate = newDate) }
    }

    fun onFinishTimeChanged(newTime: LocalDateTime) {
        recordChanges { copy(newFinishTime = newTime) }
    }

    fun onDurationChanged(duration: Duration) {
        recordChanges { copy(newDuration = duration) }
    }
}