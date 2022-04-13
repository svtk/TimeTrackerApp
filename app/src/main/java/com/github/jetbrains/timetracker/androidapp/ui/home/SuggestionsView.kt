package com.github.jetbrains.timetracker.androidapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.model.WorkActivity
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.github.jetbrains.timetracker.androidapp.util.createTestActivities

@Composable
fun SuggestionsView(
    workActivities: List<WorkActivity>,
    onCardClicked: (WorkActivity) -> Unit,
) {
    LazyColumn {
        items(workActivities) { activity ->
            SuggestionCard(
                workActivity = activity,
                onActivityClicked = onCardClicked,
            )
        }
    }
}

@Composable
fun SuggestionCard(
    workActivity: WorkActivity,
    onActivityClicked: (WorkActivity) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
            .clickable(onClick = { onActivityClicked(workActivity) })
            .fillMaxWidth(),
        elevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                DescriptionText(workActivity)
                if (workActivity.project.value.isNotEmpty()) {
                    ProjectText(workActivity)
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                        .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.FastForward,
                    modifier = Modifier.size(32.dp),
                    contentDescription = "Start",
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuggestionsPreview() {
    TimeTrackerAppTheme {
        SuggestionsView(
            workActivities = createTestActivities(),
            onCardClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SuggestionCardPreview() {
    TimeTrackerAppTheme {
        SuggestionCard(
            workActivity = createTestActivities().first(),
            onActivityClicked = {},
        )
    }
}