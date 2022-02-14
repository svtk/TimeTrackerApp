package com.example.timetrackerapp.data

import com.example.timetrackerapp.model.BlockOfWork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeBlocksOfWorkRepository : BlocksOfWorkRepository {
    // "fake" in-memory implementation
    private var runningBlockStateFlow = MutableStateFlow<BlockOfWork?>(null)
        //by mutableStateOf(null)
        private set

    private var finishedBlocksStateFlow = MutableStateFlow(listOf<BlockOfWork>())
    //by mutableStateOf(listOf<BlockOfWork>())

    private val mutex = Mutex()

    private fun findBlockById(id: Int?) = finishedBlocksStateFlow.value.find { it.id == id }

    override fun observeFinishedBlocks(): Flow<List<BlockOfWork>> {
        return finishedBlocksStateFlow
    }

    override suspend fun getFinishedBlock(id: Int): Result<BlockOfWork?> {
        return Result.success(findBlockById(id))
    }

    override suspend fun updateFinishedBlock(block: BlockOfWork) {
        mutex.withLock {
            val oldBlock = findBlockById(block.id)
            val oldBlocksState = finishedBlocksStateFlow.value
            finishedBlocksStateFlow.value = if (oldBlock != null) {
                (oldBlocksState - oldBlock + block).sortedBy { it.startTime }
            } else {
                oldBlocksState + block
            }
        }
    }

    override fun observeRunningBlock(): Flow<BlockOfWork?> {
        return runningBlockStateFlow
    }

    override suspend fun updateRunningBlock(block: BlockOfWork) {
        mutex.withLock {
            runningBlockStateFlow.value = block
        }
    }

    override suspend fun finishRunningBlock() {
        mutex.withLock {
            val newFinishedBlock = runningBlockStateFlow.value
            if (newFinishedBlock != null) {
                val nextId = (finishedBlocksStateFlow.value.maxOfOrNull { it.id } ?: 0) + 1
                finishedBlocksStateFlow.value =
                    finishedBlocksStateFlow.value + newFinishedBlock.copy(id = nextId)
            }
        }
    }
}