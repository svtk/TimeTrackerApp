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
    var currentDescription by remember { mutableStateOf("") }

    var finishedBlocks by remember { mutableStateOf(listOf<BlockOfWork>()) }

    val coroutineScope = rememberCoroutineScope()
    val runningBlockOfWorkStore = remember {
        RunningBlockOfWorkStore(
            coroutineScope,
            nextId = { (finishedBlocks.maxOfOrNull { it.id } ?: 0) + 1 }
        )
    }

    val onTextUpdate = { newText: String -> currentDescription = newText }
    val onCardClicked = { id: Int ->
        chosenBlockId = id
    }
    fun startNewBlock(description: String, project: Project, task: Task) {
        val id = runningBlockOfWorkStore.startNewTimeBlock(description, project, task)
        currentDescription = ""
        chosenBlockId = id
    }
    fun finishCurrentBlock() {
        runningBlockOfWorkStore.onFinishClicked()
        finishedBlocks =
            runningBlockOfWorkStore.blockOfWork?.let { currentBlock ->
                listOf(currentBlock) + finishedBlocks
            } ?: finishedBlocks
        runningBlockOfWorkStore.clearTimeBlock()
        chosenBlockId = null
    }
    fun startSimilarBlock(id: Int) {
        finishCurrentBlock()
        val originalBlock = finishedBlocks.first { it.id == id }
        startNewBlock(originalBlock.description.value, originalBlock.project, originalBlock.task)
    }

    if (chosenBlockId == null) {
        MainView(
            blockOfWork = runningBlockOfWorkStore.blockOfWork,
            duration = if (runningBlockOfWorkStore.blockOfWork != null)
                runningBlockOfWorkStore.getCurrentDuration().value
            else
                null,
            finishedBlocks = finishedBlocks,
            currentDescription = currentDescription,
            onTextUpdate = onTextUpdate,
            onNewBlock = { startNewBlock(currentDescription, Project(""), Task("")) },
            onCurrentBlockFinished = { finishCurrentBlock() },
            onCardClicked = onCardClicked,
            onSimilarBlockStarted = { id -> startSimilarBlock(id) },
            onCurrentBlockResumed = { runningBlockOfWorkStore.onResumeClicked() },
        )
    } else {
        if (chosenBlockId == runningBlockOfWorkStore.blockOfWork?.id) {
            BlockOfWorkDetailedView(
                blockOfWork = runningBlockOfWorkStore.blockOfWork!!,
                duration = runningBlockOfWorkStore.getCurrentDuration().value,
                onProjectChanged = runningBlockOfWorkStore::onProjectChanged,
                onTaskChanged = runningBlockOfWorkStore::onTaskChanged,
                onDescriptionChanged = runningBlockOfWorkStore::onDescriptionChanged,
                onStartClicked = runningBlockOfWorkStore::onStartClicked,
                onPauseClicked = runningBlockOfWorkStore::onPauseClicked,
                onResumeClicked = runningBlockOfWorkStore::onResumeClicked,
                onFinishClicked = ::finishCurrentBlock,
                onBackClicked = { chosenBlockId = null }
            )
        } else {
            val block = finishedBlocks.first { it.id == chosenBlockId }
            BlockOfWorkDetailedView(
                blockOfWork = block,
                duration = block.duration,
                onProjectChanged = {},
                onTaskChanged = {},
                onDescriptionChanged = {},
                onStartClicked = {},
                onPauseClicked = {},
                onResumeClicked = {},
                onFinishClicked = {},
                onBackClicked = { chosenBlockId = null }
            )
        }
    }
}