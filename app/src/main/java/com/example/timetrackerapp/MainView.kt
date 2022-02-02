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
import kotlin.time.Duration


@Composable
fun MainView(
    blockOfWork: BlockOfWork?,
    duration: Duration?,
    finishedBlocks: List<BlockOfWork>,
    currentDescription: String,
    onTextUpdate: (String) -> Unit,
    onNewBlock: () -> Unit,
    onCurrentBlockFinished: (id: Int) -> Unit,
    onCardClicked: (id: Int) -> Unit,
    onSimilarBlockStarted: (id: Int) -> Unit,
    onCurrentBlockResumed: (id: Int) -> Unit,
) {
    Column {
        Box(modifier = Modifier.padding(8.dp)) {
            if (blockOfWork == null) {
                StartingNewBlock(
                    text = currentDescription,
                    onTextUpdate = onTextUpdate,
                    onNewTask = onNewBlock
                )
            } else {
                BlockOfWorkCard(
                    blockOfWork = blockOfWork,
                    duration = duration ?: blockOfWork.duration,
                    onCardClicked = onCardClicked,
                    onStartClicked = onCurrentBlockResumed,
                    onFinishClicked = onCurrentBlockFinished,
                )
            }
        }
        BlockOfWorkListContent(
            blocks = finishedBlocks,
            onCardClicked = onCardClicked,
            onSimilarBlockStarted = onSimilarBlockStarted,
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
            duration = null,
            finishedBlocks = testTimeBlocks(),
            currentDescription = "",
            onTextUpdate = {},
            onNewBlock = {},
            onCurrentBlockFinished = {},
            onCardClicked = {},
            onCurrentBlockResumed = {},
            onSimilarBlockStarted = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenChoosingTaskPreview() {
    TimeTrackerAppTheme {
        MainView(
            blockOfWork = null,
            duration = null,
            finishedBlocks = testTimeBlocks(),
            currentDescription = "",
            onTextUpdate = {},
            onNewBlock = {},
            onCurrentBlockFinished = {},
            onCardClicked = {},
            onCurrentBlockResumed = {},
            onSimilarBlockStarted = {},
        )
    }
}