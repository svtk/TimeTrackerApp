package com.github.jetbrains.timetracker.androidapp.ui.slice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jetbrains.timetracker.androidapp.data.SlicesRepository
import com.github.jetbrains.timetracker.androidapp.model.SliceChanges
import com.github.jetbrains.timetracker.androidapp.model.WorkSlice
import com.github.jetbrains.timetracker.androidapp.model.applyChanges
import kotlinx.coroutines.launch
import java.util.*

class FinishedSliceViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    var slice by mutableStateOf<WorkSlice?>(null)
        private set

    fun updateChosenSlice(id: UUID) {
        if (slice == null || slice?.id != id) {
            viewModelScope.launch {
                slice = repository.getFinishedSlice(id).getOrNull()
            }
        }
    }

    private fun clearChosenSlice() {
        slice = null
    }

    fun onSave(changes: SliceChanges) {
        slice = slice?.applyChanges(changes)
        slice?.let { slice ->
            viewModelScope.launch {
                repository.updateFinishedSlice(slice.id, changes)
            }
        }
        clearChosenSlice()
    }
}