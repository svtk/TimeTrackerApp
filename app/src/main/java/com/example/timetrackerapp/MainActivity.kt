package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
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

// TODO
enum class ScreenState { RUNNING_BLOCK_OF_WORK, BLOCK_LIST }

@Composable
fun MainScreen() {
    var screenState by remember { mutableStateOf(ScreenState.BLOCK_LIST) }
    var currentDescription by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val runningBlockOfWorkStore = remember { RunningBlockOfWorkStore(coroutineScope) }

    var finishedBlocks by remember { mutableStateOf(listOf<BlockOfWork>()) }

    val onCurrentBlockFinished = {
        runningBlockOfWorkStore.onFinishClicked()
        finishedBlocks += runningBlockOfWorkStore.blockOfWork!!
        runningBlockOfWorkStore.clearTimeBlock()
        screenState = ScreenState.BLOCK_LIST
    }
    Column {
        if (screenState == ScreenState.BLOCK_LIST) {
            if (runningBlockOfWorkStore.blockOfWork == null) {
                StartingNewBlock(
                    text = currentDescription,
                    onTextUpdate = { newText -> currentDescription = newText },
                    onNewTask = {
                        runningBlockOfWorkStore.startNewTimeBlock(currentDescription)
                        currentDescription = ""
                        screenState = ScreenState.RUNNING_BLOCK_OF_WORK
                    }
                )
            } else {
                BlockOfWorkCard(
                    blockOfWork = runningBlockOfWorkStore.blockOfWork!!,
                    onStartClicked = runningBlockOfWorkStore::onStartClicked,
                    onPauseClicked = runningBlockOfWorkStore::onPauseClicked,
                    onResumeClicked = runningBlockOfWorkStore::onResumeClicked,
                    onFinishClicked = onCurrentBlockFinished,
                )
            }
            BlockOfWorkListContent(finishedBlocks)

        } else {
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
                onBackClicked = { screenState = ScreenState.BLOCK_LIST }
            )
        }
    }
}

@Composable
fun StartingNewBlock(
    text: String,
    onTextUpdate: (String) -> Unit,
    onNewTask: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { onTextUpdate(it) },
            label = { Text("I'm working on...") }
        )
        IconButton(onClick = onNewTask) {
            Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = "Start"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TimeTrackerAppTheme {
        MainScreen()
    }
}