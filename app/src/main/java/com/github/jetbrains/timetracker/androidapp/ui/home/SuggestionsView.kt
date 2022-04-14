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
import com.github.jetbrains.timetracker.androidapp.ui.slice.DescriptionText
import com.github.jetbrains.timetracker.androidapp.ui.slice.Subtitle
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.github.jetbrains.timetracker.androidapp.util.createTestActivities

@Composable
fun SuggestionsView(
    lastCompleted: List<WorkActivity>,
    suggestions: List<WorkActivity>,
    onCardClicked: (WorkActivity) -> Unit,
) {
    if (lastCompleted.isNotEmpty()) {
        Subtitle("Continue")
        LazyColumn {
            items(lastCompleted) { lastCompleted ->
                SuggestionCard(
                    workActivity = lastCompleted,
                    onActivityClicked = onCardClicked,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
    }
    if (suggestions.isNotEmpty()) {
        Subtitle("Quick start")
        LazyColumn {
            items(suggestions) { suggestion ->
                SuggestionCard(
                    workActivity = suggestion,
                    onActivityClicked = onCardClicked,
                )
            }
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
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
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
    val activities = createTestActivities()
    TimeTrackerAppTheme {
        SuggestionsView(
            lastCompleted = activities.take(1),
            suggestions = activities.drop(1),
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