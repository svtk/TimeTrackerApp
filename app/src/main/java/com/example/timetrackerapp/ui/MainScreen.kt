package com.example.timetrackerapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.timetrackerapp.BlockOfWorkDetailedView
import com.example.timetrackerapp.MainView
import com.example.timetrackerapp.model.Project
import com.example.timetrackerapp.model.RunningBlockViewModel
import com.example.timetrackerapp.model.Task

@Composable
fun MainScreen(
    runningBlockViewModel: RunningBlockViewModel,
    finishedBlocksViewModel: FinishedBlocksViewModel,
) {
    val finishedBlocks by finishedBlocksViewModel.finishedBlocks.collectAsState(listOf())
    fun findFinishedBlockById(id: Int?) = finishedBlocks.find { it.id == id }

    fun startNewBlock(description: String, project: Project, task: Task) {
        val id = runningBlockViewModel.startNewTimeBlock(description, project, task)
        runningBlockViewModel.updateDescription("")
        finishedBlocksViewModel.updateChosenBlock(id)
    }

    fun finishRunningBlock() {
        runningBlockViewModel.onFinishClicked()
        finishedBlocksViewModel.updateChosenBlock(id = null)
    }

    fun startSimilarBlock(id: Int) {
        finishRunningBlock()
        val originalBlock = findFinishedBlockById(id) ?: return
        startNewBlock(originalBlock.description.value, originalBlock.project, originalBlock.task)
    }

    val runningBlock = runningBlockViewModel.blockOfWork
    if (runningBlock != null && finishedBlocksViewModel.chosenBlockId == runningBlock.id) {
        BlockOfWorkDetailedView(
            blockOfWork = runningBlock,
            duration = runningBlockViewModel.getCurrentDuration().value,
            onProjectChanged = runningBlockViewModel::onProjectChanged,
            onTaskChanged = runningBlockViewModel::onTaskChanged,
            onDescriptionChanged = runningBlockViewModel::onDescriptionChanged,
            onStartClicked = runningBlockViewModel::onStartClicked,
            onPauseClicked = runningBlockViewModel::onPauseClicked,
            onResumeClicked = runningBlockViewModel::onResumeClicked,
            onFinishClicked = ::finishRunningBlock,
            onBackClicked = { finishedBlocksViewModel.updateChosenBlock(null) }
        )
        return
    }
    val chosenFinishedBlock = findFinishedBlockById(finishedBlocksViewModel.chosenBlockId)
    if (chosenFinishedBlock != null) {
        BlockOfWorkDetailedView(
            blockOfWork = chosenFinishedBlock,
            duration = chosenFinishedBlock.duration,
            onProjectChanged = {},
            onTaskChanged = {},
            onDescriptionChanged = {},
            onStartClicked = {},
            onPauseClicked = {},
            onResumeClicked = {},
            onFinishClicked = {},
            onBackClicked = { finishedBlocksViewModel.updateChosenBlock(null) }
        )
        return
    }
    MainView(
        blockOfWork = runningBlock,
        duration = if (runningBlock != null)
            runningBlockViewModel.getCurrentDuration().value
        else
            null,
        finishedBlocks = finishedBlocks,
        currentDescription = runningBlockViewModel.currentDescription,
        onDescriptionUpdate = runningBlockViewModel::updateDescription,
        onNewBlock = { startNewBlock(runningBlockViewModel.currentDescription, Project(""), Task("")) },
        onCardClicked = finishedBlocksViewModel::updateChosenBlock,
        onSimilarBlockStarted = { id -> startSimilarBlock(id) },
        onCurrentBlockResumed = runningBlockViewModel::onResumeClicked,
        onCurrentBlockFinished = ::finishRunningBlock,
    )
}