package com.example.timetrackerapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timetrackerapp.data.SlicesRepository
import com.example.timetrackerapp.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.Duration.Companion.seconds

class HomeViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    var currentDescription by mutableStateOf("")
        private set

    private val tickHandler = TickHandler(viewModelScope, 1.seconds)
    val runningSlice: Flow<WorkSlice?> =
        repository
            .observeRunningSlice()
            .convertToWorkSliceAndEmitEverySecond(viewModelScope, tickHandler.tickFlow)

    val finishedSlices: StateFlow<List<WorkSlice>> =
        repository.observeFinishedSlices().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    fun updateDescription(newText: String) {
        currentDescription = newText
    }

    private fun startNewSlice(
        description: String, project: Project, task: Task
    ) {
        updateDescription("")
        viewModelScope.launch {
            repository.startRunningSlice(
                Description(description),
                task,
                project,
            )
        }
    }

    fun startNewSlice() = startNewSlice(
        currentDescription,
        Project(""),
        Task(""),
    )

    fun startSimilarSlice(id: UUID) {
        finishRunningSlice()
        viewModelScope.launch {
            repository.getFinishedSlice(id).getOrNull()?.let { originalSlice ->
                startNewSlice(
                    originalSlice.description.value,
                    originalSlice.project,
                    originalSlice.task
                )
            }
        }
    }

    fun finishRunningSlice() {
        viewModelScope.launch {
            repository.finishRunningSlice()
        }
    }

    companion object {
        fun provideFactory(
            repository: SlicesRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
        }
    }
}