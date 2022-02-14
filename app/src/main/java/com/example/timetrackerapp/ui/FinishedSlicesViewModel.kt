package com.example.timetrackerapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timetrackerapp.data.SlicesRepository
import com.example.timetrackerapp.model.WorkSlice
import com.example.timetrackerapp.model.Description
import com.example.timetrackerapp.model.Project
import com.example.timetrackerapp.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinishedSlicesViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    var chosenSliceId by mutableStateOf<Int?>(null)
        private set
    fun updateChosenSlice(id: Int?) {
        chosenSliceId = id
    }

    val finishedSlices: Flow<List<WorkSlice>> =
        repository.observeFinishedSlices().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private fun updateSlice(
        id: Int, transform: WorkSlice.() -> WorkSlice
    ) {
        viewModelScope.launch {
            val result = repository.getFinishedSlice(id)
            result.getOrNull()?.let { slice ->
                repository.updateFinishedSlice(slice.transform())
            }
        }
    }

    fun onProjectChanged(id: Int, projectName: String) {
        updateSlice(id) { copy(project = Project(projectName)) }
    }

    fun onTaskChanged(id: Int, taskName: String) {
        updateSlice(id) { copy(task = Task(taskName)) }
    }

    fun onDescriptionChanged(id: Int, description: String) {
        updateSlice(id) { copy(description = Description(description)) }
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