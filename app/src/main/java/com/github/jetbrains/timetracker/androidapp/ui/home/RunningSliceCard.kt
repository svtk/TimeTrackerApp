package com.github.jetbrains.timetracker.androidapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.model.WorkActivity
import com.github.jetbrains.timetracker.androidapp.model.WorkSlice
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
                DescriptionText(slice)
                if (slice.project.value.isNotEmpty()) {
                    ProjectText(slice)
                }
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

@Composable
fun ProjectText(slice: WorkSlice) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            style = MaterialTheme.typography.caption,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            text = slice.project.value +
                    if (slice.task.value.isNotEmpty()) ": ${slice.task.value}" else ""
        )
    }
}

@Composable
fun ProjectText(workActivity: WorkActivity) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            style = MaterialTheme.typography.caption,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            text = workActivity.project.value +
                    if (workActivity.task.value.isNotEmpty()) ": ${workActivity.task.value}" else ""
        )
    }
}


@Composable
fun DescriptionText(slice: WorkSlice) {
    Text(
        style = MaterialTheme.typography.body1,
        overflow = TextOverflow.Ellipsis,
        softWrap = false,
        text = slice.description.value,
    )
}
// TODO
@Composable
fun DescriptionText(workActivity: WorkActivity) {
    Text(
        style = MaterialTheme.typography.body1,
        overflow = TextOverflow.Ellipsis,
        softWrap = false,
        text = workActivity.description.value,
    )
}

@Composable
fun ActionButton(
    modifier: Modifier,
    contentDescription: String,
    imageVector: ImageVector,
    color: Color,
    size: Dp,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(size),
            tint = color,
        )
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