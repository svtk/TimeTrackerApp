package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.example.timetrackerapp.model.BlockOfWork
import com.example.timetrackerapp.model.Project
import com.example.timetrackerapp.model.RunningBlockOfWorkStore
import com.example.timetrackerapp.model.Task
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeTrackerAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Time Tracker") })
                    },
                ) {
                    Surface(color = MaterialTheme.colors.background) {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var chosenBlockId by remember { mutableStateOf<Int?>(null) }
    fun updateChosenBlock(id: Int?) {
        chosenBlockId = id
    }

    var currentDescription by remember { mutableStateOf("") }
    fun updateDescription(newText: String) {
        currentDescription = newText
    }

    var finishedBlocks by remember { mutableStateOf(listOf<BlockOfWork>()) }
    fun findFinishedBlockById(id: Int?) = finishedBlocks.find { it.id == id }

    val coroutineScope = rememberCoroutineScope()
    val runningBlockOfWorkStore = remember {
        RunningBlockOfWorkStore(
            coroutineScope,
            nextId = { (finishedBlocks.maxOfOrNull { it.id } ?: 0) + 1 }
        )
    }

    fun startNewBlock(description: String, project: Project, task: Task) {
        val id = runningBlockOfWorkStore.startNewTimeBlock(description, project, task)
        updateChosenBlock(id)
        updateDescription("")
    }

    fun finishCurrentBlock() {
        runningBlockOfWorkStore.onFinishClicked()
        finishedBlocks =
            runningBlockOfWorkStore.blockOfWork?.let { currentBlock ->
                listOf(currentBlock) + finishedBlocks
            } ?: finishedBlocks
        runningBlockOfWorkStore.clearTimeBlock()
        updateChosenBlock(null)
    }

    fun startSimilarBlock(id: Int) {
        finishCurrentBlock()
        val originalBlock = findFinishedBlockById(id) ?: return
        startNewBlock(originalBlock.description.value, originalBlock.project, originalBlock.task)
    }

    val runningBlock = runningBlockOfWorkStore.blockOfWork
    if (runningBlock != null && chosenBlockId == runningBlock.id) {
        BlockOfWorkDetailedView(
            blockOfWork = runningBlock,
            duration = runningBlockOfWorkStore.getCurrentDuration().value,
            onProjectChanged = runningBlockOfWorkStore::onProjectChanged,
            onTaskChanged = runningBlockOfWorkStore::onTaskChanged,
            onDescriptionChanged = runningBlockOfWorkStore::onDescriptionChanged,
            onStartClicked = runningBlockOfWorkStore::onStartClicked,
            onPauseClicked = runningBlockOfWorkStore::onPauseClicked,
            onResumeClicked = runningBlockOfWorkStore::onResumeClicked,
            onFinishClicked = ::finishCurrentBlock,
            onBackClicked = { updateChosenBlock(null) }
        )
        return
    }
    val chosenFinishedBlock = findFinishedBlockById(chosenBlockId)
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
            onBackClicked = { updateChosenBlock(null) }
        )
        return
    }
    MainView(
        blockOfWork = runningBlock,
        duration = if (runningBlock != null)
            runningBlockOfWorkStore.getCurrentDuration().value
        else
            null,
        finishedBlocks = finishedBlocks,
        currentDescription = currentDescription,
        onDescriptionUpdate = ::updateDescription,
        onNewBlock = { startNewBlock(currentDescription, Project(""), Task("")) },
        onCardClicked = ::updateChosenBlock,
        onSimilarBlockStarted = { id -> startSimilarBlock(id) },
        onCurrentBlockResumed = runningBlockOfWorkStore::onResumeClicked,
        onCurrentBlockFinished = ::finishCurrentBlock,
    )
}