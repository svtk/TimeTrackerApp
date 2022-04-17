package com.github.jetbrains.timetracker.androidapp.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.model.*
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.github.jetbrains.timetracker.androidapp.util.createTestSlices
import com.github.jetbrains.timetracker.androidapp.util.createTestTimeRanges
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import java.util.*

@Composable
fun LogsView(
    logsViewModel: LogsViewModel = getViewModel(),
    navigateToRunningSlice: () -> Unit,
    navigateToChosenSlice: (UUID) -> Unit,
) {
    val finishedSlicesByDays = logsViewModel.finishedSlices
//    val runningSlice by logsViewModel.runningSlice.collectAsState(null)
    LogsView(
        timeRangesTabs = logsViewModel.timeRanges,
        finishedSlicesByDays = finishedSlicesByDays,
        onChangingTimeRange = logsViewModel::updateTimeRange,
        onCardClicked = navigateToChosenSlice,
    )
}

@Composable
fun LogsView(
    timeRangesTabs: List<TimeRange>,
    finishedSlicesByDays: WorkSlicesByDays?,
    onChangingTimeRange: (Int) -> Unit,
    onCardClicked: (id: UUID) -> Unit,
) {
    Column {
        var selectedIndex by remember { mutableStateOf(timeRangesTabs.lastIndex) }
        val coroutineScope = rememberCoroutineScope()
        val lazyListState: LazyListState = rememberLazyListState()
        Column {
            ScrollableTabRow(selectedTabIndex = selectedIndex) {
                timeRangesTabs.forEachIndexed { index, timeRange ->
                    Tab(
                        text = { Text(
                            text = timeRange.title,
                            style = MaterialTheme.typography.overline,
                        ) },
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            onChangingTimeRange(index)
                            coroutineScope.launch {
                                lazyListState.scrollToItem(0)
                            }
                        }
                    )
                }
            }
        }
        if (finishedSlicesByDays != null) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                SliceListView(
                    slicesByDays = finishedSlicesByDays,
                    state = lazyListState,
                    onCardClicked = onCardClicked,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenRunningTaskPreview() {
    TimeTrackerAppTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            LogsView(
                timeRangesTabs = createTestTimeRanges(),
                finishedSlicesByDays = createTestSlices().toWorkSlicesByDays(),
                onChangingTimeRange = {},
                onCardClicked = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "HomeScreenChoosingTaskPreviewDark"
)
@Composable
fun HomeScreenChoosingTaskPreview() {
    TimeTrackerAppTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            LogsView(
                timeRangesTabs = createTestTimeRanges(),
                finishedSlicesByDays = createTestSlices().toWorkSlicesByDays(),
                onChangingTimeRange = {},
                onCardClicked = {},
            )
        }
    }
}