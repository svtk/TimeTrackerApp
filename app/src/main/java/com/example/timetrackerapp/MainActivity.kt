package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    val onCurrentBlockFinished = {
        runningBlockOfWorkStore.onFinishClicked()
        finishedBlocks += runningBlockOfWorkStore.blockOfWork!!
        runningBlockOfWorkStore.clearTimeBlock()
        chosenBlockId = null
    }
    if (chosenBlockId == null) {
        Column {
            // TODO: sync padding, add preview
            Box(modifier = Modifier.padding(4.dp)) {
                if (runningBlockOfWorkStore.blockOfWork == null) {
                    StartingNewBlock(
                        text = currentDescription,
                        onTextUpdate = { newText -> currentDescription = newText },
                        onNewTask = {
                            val id = runningBlockOfWorkStore.startNewTimeBlock(currentDescription)
                            currentDescription = ""
                            chosenBlockId = id
                        }
                    )
                } else {
                    BlockOfWorkCard(
                        blockOfWork = runningBlockOfWorkStore.blockOfWork!!,
                        onCardClicked = {
                            chosenBlockId = runningBlockOfWorkStore.blockOfWork!!.id
                        },
                        onStartClicked = runningBlockOfWorkStore::onStartClicked,
                        onPauseClicked = runningBlockOfWorkStore::onPauseClicked,
                        onResumeClicked = runningBlockOfWorkStore::onResumeClicked,
                        onFinishClicked = onCurrentBlockFinished,
                    )
                }
            }
            BlockOfWorkListContent(
                blocks = finishedBlocks,
                onBlockClicked = { id -> chosenBlockId = id }
            )
        }
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

@Composable
fun StartingNewBlock(
    text: String,
    onTextUpdate: (String) -> Unit,
    onNewTask: () -> Unit
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
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