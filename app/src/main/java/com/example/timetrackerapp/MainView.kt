package com.example.timetrackerapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timetrackerapp.model.*
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme


@Composable
fun MainView(
    blockOfWork: BlockOfWork?,
    finishedBlocks: List<BlockOfWork>,
    currentDescription: String,
    onTextUpdate: (String) -> Unit,
    onNewTask: () -> Unit,
    onCurrentBlockFinished: () -> Unit,
    onCardClicked: (id: Int) -> Unit,
    onStartClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
) {
    Column {
        Box(modifier = Modifier.padding(8.dp)) {
            if (blockOfWork == null) {
                StartingNewBlock(
                    text = currentDescription,
                    onTextUpdate = onTextUpdate,
                    onNewTask = onNewTask
                )
            } else {
                BlockOfWorkCard(
                    blockOfWork = blockOfWork,
                    onCardClicked = { onCardClicked(blockOfWork.id) },
                    onStartClicked = onStartClicked,
                    onPauseClicked = onPauseClicked,
                    onResumeClicked = onResumeClicked,
                    onFinishClicked = onCurrentBlockFinished,
                )
            }
        }
        BlockOfWorkListContent(
            blocks = finishedBlocks,
            onBlockClicked = { id -> onCardClicked(id) }
        )
    }
}

@Composable
fun StartingNewBlock(
    text: String,
    onTextUpdate: (String) -> Unit,
    onNewTask: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.8f),
            value = text,
            onValueChange = { onTextUpdate(it) },
            label = { Text("I'm working on...") },
        )
        IconButton(
            onClick = onNewTask,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = "Start"
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenRunningTaskPreview() {
    TimeTrackerAppTheme {
        MainView(
            blockOfWork = BlockOfWork(
                0,
                Project("my Project"),
                Task("my Task"),
                Description("my description"),
                BlockOfWork.State.RUNNING,
                testTimeIntervals(),
            ),
            finishedBlocks = testTimeBlocks(),
            currentDescription = "",
            onTextUpdate = {},
            onNewTask = {},
            onCurrentBlockFinished = {},
            onCardClicked = {},
            onStartClicked = {},
            onPauseClicked = {},
            onResumeClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenChoosingTaskPreview() {
    TimeTrackerAppTheme {
        MainView(
            blockOfWork = null,
            finishedBlocks = testTimeBlocks(),
            currentDescription = "",
            onTextUpdate = {},
            onNewTask = {},
            onCurrentBlockFinished = {},
            onCardClicked = {},
            onStartClicked = {},
            onPauseClicked = {},
            onResumeClicked = {},
        )
    }
}