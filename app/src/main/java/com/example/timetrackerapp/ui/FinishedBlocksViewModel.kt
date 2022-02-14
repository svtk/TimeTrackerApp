package com.example.timetrackerapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timetrackerapp.data.BlocksOfWorkRepository
import com.example.timetrackerapp.model.BlockOfWork
import com.example.timetrackerapp.model.Description
import com.example.timetrackerapp.model.Project
import com.example.timetrackerapp.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinishedBlocksViewModel(
    private val repository: BlocksOfWorkRepository
) : ViewModel() {

    var chosenBlockId by mutableStateOf<Int?>(null)
    fun updateChosenBlock(id: Int?) {
        chosenBlockId = id
    }

    val finishedBlocks: Flow<List<BlockOfWork>> =
        repository.observeFinishedBlocks().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private fun updateBlock(
        id: Int, transform: BlockOfWork.() -> BlockOfWork
    ) {
        viewModelScope.launch {
            val result = repository.getFinishedBlock(id)
            result.getOrNull()?.let { block ->
                repository.updateFinishedBlock(block.transform())
            }
        }
    }

    fun onProjectChanged(id: Int, projectName: String) {
        updateBlock(id) { copy(project = Project(projectName)) }
    }

    fun onTaskChanged(id: Int, taskName: String) {
        updateBlock(id) { copy(task = Task(taskName)) }
    }

    fun onDescriptionChanged(id: Int, description: String) {
        updateBlock(id) { copy(description = Description(description)) }
    }

    companion object {
        fun provideFactory(
            repository: BlocksOfWorkRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return FinishedBlocksViewModel(repository) as T
                }
            }
        }
    }
}