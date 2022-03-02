package com.example.timetrackerapp.ui.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

class TickHandler(
    externalScope: CoroutineScope,
    tickInterval: Duration
) {
    private val _tickFlow = MutableSharedFlow<Unit>()
    val tickFlow: SharedFlow<Unit> get() = _tickFlow

    init {
        externalScope.launch {
            while(true) {
                delay(tickInterval)
                _tickFlow.emit(Unit)
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> Flow<T>.transformAndEmitRegularly(
    scope: CoroutineScope,
    tickFlow: Flow<Unit>,
    transform: (T) -> R
): Flow<R?> {
    return channelFlow {
        val mutex = Mutex()
        var lastElement: T? = null
        scope.launch {
            tickFlow.collect {
                mutex.withLock {
                    send(lastElement?.let(transform))
                }
            }
        }
        collect { element ->
            send(transform(element))
            mutex.withLock {
                lastElement = element
            }
        }
    }
}
