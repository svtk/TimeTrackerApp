package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.example.timetrackerapp.data.FakeBlocksOfWorkRepository
import com.example.timetrackerapp.model.Project
import com.example.timetrackerapp.model.RunningBlockViewModel
import com.example.timetrackerapp.model.Task
import com.example.timetrackerapp.ui.FinishedBlocksViewModel
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO use dependency injection
        val repository = FakeBlocksOfWorkRepository()

        val runningBlockViewModel: RunningBlockViewModel by
            viewModels(factoryProducer = { RunningBlockViewModel.provideFactory(repository) })
        val finishedBlocksViewModel: FinishedBlocksViewModel by
            viewModels(factoryProducer = { FinishedBlocksViewModel.provideFactory(repository) })

        setContent {
            TimeTrackerAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Time Tracker") })
                    },
                ) {
                    Surface(color = MaterialTheme.colors.background) {
                        MainScreen(
                            runningBlockViewModel,
                            finishedBlocksViewModel,
                        )
                    }
                }
            }
        }
    }
}

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