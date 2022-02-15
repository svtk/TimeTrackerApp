package com.example.timetrackerapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timetrackerapp.data.SlicesRepository
import com.example.timetrackerapp.model.*
import com.example.timetrackerapp.util.replaceDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration

class FinishedSlicesViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    var chosenSliceId by mutableStateOf<Int?>(null)
        private set

    fun updateChosenSlice(id: Int?) {
        chosenSliceId = id
    }

    val finishedSlices: Flow<List<FinishedSlice>> =
        repository.observeFinishedSlices().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private fun updateSlice(
        transform: FinishedSlice.() -> FinishedSlice
    ) {
        chosenSliceId?.let { id ->
            viewModelScope.launch {
                val result = repository.getFinishedSlice(id)
                result.getOrNull()?.let { slice ->
                    repository.updateFinishedSlice(slice.transform())
                }
            }
        }
    }

    fun onProjectChanged(projectName: String) {
        updateSlice { copy(project = Project(projectName)) }
    }

    fun onTaskChanged(taskName: String) {
        updateSlice { copy(task = Task(taskName)) }
    }

    fun onDescriptionChanged(description: String) {
        updateSlice { copy(description = Description(description)) }
    }

    fun onStartDateChanged(newDate: LocalDate) {
        updateSlice {
            val newStartTime = startTime.replaceDate(newDate)
            copy(startInstant = newStartTime.toInstant(TimeZone.currentSystemDefault()))
        }
    }

    fun onStartTimeChanged(newTime: LocalDateTime) {
        updateSlice { copy(startInstant = newTime.toInstant(TimeZone.currentSystemDefault())) }
    }

    fun onFinishDateChanged(newDate: LocalDate) {
        updateSlice {
            val newFinishTime = finishTime.replaceDate(newDate)
            copy(startInstant = newFinishTime.toInstant(TimeZone.currentSystemDefault()))
        }
    }

    fun onFinishTimeChanged(newTime: LocalDateTime) {
        updateSlice { copy(finishInstant = newTime.toInstant(TimeZone.currentSystemDefault())) }
    }

    fun onDurationChanged(duration: Duration) {
        updateSlice { copy(duration = duration) }
    }

    companion object {
        fun provideFactory(
            repository: SlicesRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return FinishedSlicesViewModel(repository) as T
                }
            }
        }
    }
}