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
    onDescriptionUpdate: (String) -> Unit,
    onNewBlock: () -> Unit,
    onCardClicked: (id: Int) -> Unit,
    onSimilarBlockStarted: (id: Int) -> Unit,
    onCurrentBlockResumed: () -> Unit,
    onCurrentBlockFinished: () -> Unit,
) {
    Column {
        Box(modifier = Modifier.padding(8.dp)) {
            if (blockOfWork == null) {
                StartingNewBlock(
                    description = currentDescription,
                    onDescriptionUpdate = onDescriptionUpdate,
                    onNewBlock = onNewBlock
                )
            } else {
                BlockOfWorkCard(
                    blockOfWork = blockOfWork,
                    duration = duration ?: blockOfWork.duration,
                    onCardClicked = onCardClicked,
                    onStartClicked = { onCurrentBlockResumed() },
                    onFinishClicked = { onCurrentBlockFinished() },
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
    description: String,
    onDescriptionUpdate: (String) -> Unit,
    onNewBlock: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.8f),
            value = description,
            onValueChange = { onDescriptionUpdate(it) },
            label = { Text("I'm working on...") },
        )
        IconButton(
            onClick = onNewBlock,
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
            onDescriptionUpdate = {},
            onNewBlock = {},
            onCardClicked = {},
            onSimilarBlockStarted = {},
            onCurrentBlockResumed = {},
        ) {}
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
            onDescriptionUpdate = {},
            onNewBlock = {},
            onCardClicked = {},
            onSimilarBlockStarted = {},
            onCurrentBlockResumed = {},
        ) {}
    }
}