package com.example.timetrackerapp.data

import com.example.timetrackerapp.model.BlockOfWork
import kotlinx.coroutines.flow.Flow

interface BlocksOfWorkRepository {

    fun observeFinishedBlocks(): Flow<List<BlockOfWork>>

    suspend fun getFinishedBlock(id: Int): Result<BlockOfWork?>

    suspend fun updateFinishedBlock(block: BlockOfWork)

    fun observeRunningBlock(): Flow<BlockOfWork?>

    suspend fun updateRunningBlock(block: BlockOfWork)

    suspend fun finishRunningBlock()
}