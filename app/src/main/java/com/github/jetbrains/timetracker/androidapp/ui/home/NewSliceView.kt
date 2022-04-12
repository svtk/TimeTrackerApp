package com.github.jetbrains.timetracker.androidapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.model.WorkSlice
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.github.jetbrains.timetracker.androidapp.util.changeBorderColor
import com.github.jetbrains.timetracker.androidapp.util.createTestSlices
import org.koin.androidx.compose.getViewModel
import java.util.*

@Composable
fun NewSliceView(
    timerViewModel: TimerViewModel = getViewModel(),
    navigateToRunningSlice: () -> Unit,
) {
    val finishedSlices by timerViewModel.finishedSlices.collectAsState()
    NewSliceView(
        finishedSlices = finishedSlices,
        currentDescription = timerViewModel.currentDescription,
        onDescriptionUpdate = timerViewModel::updateDescription,
        onNewSlice = {
            timerViewModel.startNewSlice()
            navigateToRunningSlice()
        },
        onCardClicked = {},
        onSimilarSliceStarted = timerViewModel::startSimilarSlice,
    )
}

@Composable
fun NewSliceView(
    finishedSlices: List<WorkSlice>,
    currentDescription: String,
    onDescriptionUpdate: (String) -> Unit,
    onNewSlice: () -> Unit,
    onCardClicked: (id: UUID) -> Unit,
    onSimilarSliceStarted: (id: UUID) -> Unit,
) {
    Column {
        Box(modifier = Modifier.padding(8.dp)) {
            StartingNewSlice(
                description = currentDescription,
                onDescriptionUpdate = onDescriptionUpdate,
                onNewSlice = onNewSlice
            )
        }
        // TODO suggestions
        SliceListView(
            slices = finishedSlices,
            onCardClicked = onCardClicked,
            onSimilarSliceStarted = onSimilarSliceStarted,
        )
    }
}

@Composable
fun StartingNewSlice(
    description: String,
    onDescriptionUpdate: (String) -> Unit,
    onNewSlice: () -> Unit
) {
    Card(Modifier.padding(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                value = description,
                onValueChange = { onDescriptionUpdate(it) },
                label = { Text("I'm working on...") },
                colors = TextFieldDefaults.changeBorderColor(),
            )
            IconButton(
                onClick = onNewSlice,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayCircle,
                    contentDescription = "Start",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colors.secondary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    TimeTrackerAppTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            NewSliceView(
                finishedSlices = createTestSlices(),
                currentDescription = "",
                onDescriptionUpdate = {},
                onNewSlice = {},
                onCardClicked = {},
                onSimilarSliceStarted = {},
            )
        }
    }
}