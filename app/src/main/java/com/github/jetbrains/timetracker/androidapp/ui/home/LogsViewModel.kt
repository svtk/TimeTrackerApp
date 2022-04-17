package com.github.jetbrains.timetracker.androidapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jetbrains.timetracker.androidapp.data.SlicesRepository
import com.github.jetbrains.timetracker.androidapp.model.TimeRange
import com.github.jetbrains.timetracker.androidapp.model.WorkSlicesByDays
import com.github.jetbrains.timetracker.androidapp.model.createRangeForToday
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LogsViewModel(
    private val repository: SlicesRepository
) : ViewModel() {

    var timeRanges by mutableStateOf<List<TimeRange>>(listOf())
        private set

    var chosenTimeRange by mutableStateOf<TimeRange?>(null)
        private set

    var finishedSlices by mutableStateOf<WorkSlicesByDays?>(null)
        private set

    init {
        viewModelScope.launch {
            timeRanges = repository.getTimeRanges()
            updateTimeRange(timeRanges.lastIndex)
        }
    }

    val todaySlices: StateFlow<WorkSlicesByDays> =
        repository.observeFinishedSlices(createRangeForToday()).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            WorkSlicesByDays()
        )


    fun updateTimeRange(index: Int) {
        chosenTimeRange = timeRanges.getOrNull(index)
        finishedSlices = null
        viewModelScope.launch {
            chosenTimeRange?.let { timeRange ->
                finishedSlices = repository.getFinishedSlices(timeRange).getOrNull()
            }
        }
    }
}

