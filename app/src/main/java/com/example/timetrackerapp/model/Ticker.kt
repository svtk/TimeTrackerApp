package com.example.timetrackerapp.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class TickHandler(
    externalScope: CoroutineScope,
    tickIntervalMs: Long = 1000
) {
    private val _tickFlow = MutableSharedFlow<Unit>()
    val tickFlow: SharedFlow<Unit> get() = _tickFlow

    init {
        externalScope.launch {
            while(true) {
                delay(tickIntervalMs)
                _tickFlow.emit(Unit)
            }
        }
    }
}