package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.example.timetrackerapp.model.BlockOfWork
import com.example.timetrackerapp.model.RunningBlockOfWorkStore
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
    val onNewBlock = {
        val id = runningBlockOfWorkStore.startNewTimeBlock(currentDescription)
        currentDescription = ""
        chosenBlockId = id
    }
    val onCurrentBlockFinished = {
        runningBlockOfWorkStore.onFinishClicked()
        finishedBlocks += runningBlockOfWorkStore.blockOfWork!!
        runningBlockOfWorkStore.clearTimeBlock()
        chosenBlockId = null
    }

    if (chosenBlockId == null) {
        MainView(
            runningBlockOfWorkStore.blockOfWork,
            finishedBlocks,
            currentDescription,
            onTextUpdate,
            onNewBlock,
            onCurrentBlockFinished,
            onCardClicked,
            runningBlockOfWorkStore::onStartClicked,
            runningBlockOfWorkStore::onPauseClicked,
            runningBlockOfWorkStore::onResumeClicked,
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
                onFinishClicked = onCurrentBlockFinished,
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