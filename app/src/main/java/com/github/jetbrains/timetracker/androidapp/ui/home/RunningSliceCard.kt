package com.github.jetbrains.timetracker.androidapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.model.WorkSlice
import com.github.jetbrains.timetracker.androidapp.ui.slice.ActionButton
import com.github.jetbrains.timetracker.androidapp.ui.slice.DescriptionText
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.github.jetbrains.timetracker.androidapp.util.createTestRunningSlice
import com.github.jetbrains.timetracker.androidapp.util.renderDuration
import kotlin.time.Duration

@Composable
fun RunningSliceCard(
    slice: WorkSlice,
    duration: Duration,
    onCardClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onFinishClicked: () -> Unit,
) {
    Card(Modifier.padding(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .clickable(onClick = onCardClicked)
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.h4,
                    text = duration.renderDuration(slice.state),
                )
                DescriptionText(slice.activity)
            }
            val size = 48.dp
            if (slice.state == WorkSlice.State.RUNNING) {
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    contentDescription = "Finish",
                    imageVector = Icons.Filled.StopCircle,
                    color = MaterialTheme.colors.primary,
                    size = size,
                    onClick = onFinishClicked,
                )
            } else {
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    contentDescription = "Resume",
                    imageVector = Icons.Filled.PlayCircle,
                    color = MaterialTheme.colors.secondary,
                    size = size,
                    onClick = onResumeClicked,
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RunningSliceCardPreview() {
    TimeTrackerAppTheme {
        val slice = createTestRunningSlice()
        RunningSliceCard(
            slice = slice,
            duration = slice.duration,
            onCardClicked = {},
            onResumeClicked = {},
            onFinishClicked = {},
        )
    }
}