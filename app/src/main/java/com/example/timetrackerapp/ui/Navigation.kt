package com.example.timetrackerapp.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timetrackerapp.data.SlicesRepository
import com.example.timetrackerapp.ui.UIState.*

enum class UIState {
    HOME, RUNNING_SLICE, FINISHED_SLICE
}

@Composable
fun Navigation(
    repository: SlicesRepository,
) {
    var uiState by remember { mutableStateOf(HOME) }
    var chosenSliceId by remember { mutableStateOf<Int?>(null)}
    when (uiState) {
        RUNNING_SLICE -> RunningSliceView(
            runningSliceViewModel = viewModel(
                factory = RunningSliceViewModel.provideFactory(
                    repository
                )
            ),
            navigateToHome = { uiState = HOME },
        )
        FINISHED_SLICE -> FinishedSliceView(
            id = chosenSliceId!!,
            finishedSliceViewModel = viewModel(
                factory = FinishedSliceViewModel.provideFactory(
                    repository
                )
            ),
            navigateToHome = { uiState = HOME },
        )
        HOME -> MainView(
            homeViewModel = viewModel(factory = HomeViewModel.provideFactory(repository)),
            navigateToRunningSlice = { uiState = RUNNING_SLICE },
            navigateToChosenSlice = { id -> chosenSliceId = id; uiState = FINISHED_SLICE },
        )
    }
}