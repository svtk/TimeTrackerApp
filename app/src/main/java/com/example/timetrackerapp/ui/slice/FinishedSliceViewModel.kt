package com.example.timetrackerapp.ui.slice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timetrackerapp.data.SlicesRepository
import com.example.timetrackerapp.model.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.util.*
import kotlin.time.Duration

class FinishedSliceViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    var slice by mutableStateOf<WorkSlice?>(null)
        private set

    private var sliceChanges: SliceChanges? by mutableStateOf(null)

    fun updateChosenSlice(id: UUID) {
        if (slice == null || slice?.id != id) {
            viewModelScope.launch {
                slice = repository.getFinishedSlice(id).getOrNull()
            }
        }
    }

    private fun clearChosenSlice() {
        slice = null
        sliceChanges = null
    }

    private inline fun recordChanges(update: SliceChanges.() -> SliceChanges) {
        sliceChanges = (sliceChanges ?: SliceChanges()).update()
        slice = slice?.applyChanges(sliceChanges)
    }

    fun onSave() {
        slice?.let { slice ->
            sliceChanges?.let { changes ->
                viewModelScope.launch {
                    repository.updateFinishedSlice(slice.id, changes)
                }
            }
        }
        clearChosenSlice()
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

    companion object {
        fun provideFactory(
            repository: SlicesRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FinishedSliceViewModel(repository) as T
                }
            }
        }
    }
}