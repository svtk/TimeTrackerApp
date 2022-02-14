package com.example.timetrackerapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.timetrackerapp.model.Project
import com.example.timetrackerapp.model.Task

@Composable
fun MainScreen(
    runningSliceViewModel: RunningSliceViewModel,
    finishedSlicesViewModel: FinishedSlicesViewModel,
) {
    val finishedSlices by finishedSlicesViewModel.finishedSlices.collectAsState(listOf())
    fun findFinishedSliceById(id: Int?) = finishedSlices.find { it.id == id }

    fun startNewSlice(description: String, project: Project, task: Task) {
        val id = runningSliceViewModel.startNewSlice(description, project, task)
        runningSliceViewModel.updateDescription("")
        finishedSlicesViewModel.updateChosenSlice(id)
    }

    fun finishRunningSlice() {
        runningSliceViewModel.onFinishClicked()
        finishedSlicesViewModel.updateChosenSlice(id = null)
    }

    fun startSimilarSlice(id: Int) {
        finishRunningSlice()
        val originalSlice = findFinishedSliceById(id) ?: return
        startNewSlice(originalSlice.description.value, originalSlice.project, originalSlice.task)
    }

    val runningSlice = runningSliceViewModel.slice
    if (runningSlice != null && finishedSlicesViewModel.chosenSliceId == runningSlice.id) {
        SliceDetailedView(
            slice = runningSlice,
            duration = runningSliceViewModel.getCurrentDuration().value,
            onProjectChanged = runningSliceViewModel::onProjectChanged,
            onTaskChanged = runningSliceViewModel::onTaskChanged,
            onDescriptionChanged = runningSliceViewModel::onDescriptionChanged,
            onStartClicked = runningSliceViewModel::onStartClicked,
            onPauseClicked = runningSliceViewModel::onPauseClicked,
            onResumeClicked = runningSliceViewModel::onResumeClicked,
            onFinishClicked = ::finishRunningSlice,
            onBackClicked = { finishedSlicesViewModel.updateChosenSlice(null) }
        )
        return
    }
    val chosenFinishedSlice = findFinishedSliceById(finishedSlicesViewModel.chosenSliceId)
    if (chosenFinishedSlice != null) {
        SliceDetailedView(
            slice = chosenFinishedSlice,
            duration = chosenFinishedSlice.duration,
            onProjectChanged = {},
            onTaskChanged = {},
            onDescriptionChanged = {},
            onStartClicked = {},
            onPauseClicked = {},
            onResumeClicked = {},
            onFinishClicked = {},
            onBackClicked = { finishedSlicesViewModel.updateChosenSlice(null) }
        )
        return
    }
    MainView(
        slice = runningSlice,
        duration = if (runningSlice != null)
            runningSliceViewModel.getCurrentDuration().value
        else
            null,
        finishedSlices = finishedSlices,
        currentDescription = runningSliceViewModel.currentDescription,
        onDescriptionUpdate = runningSliceViewModel::updateDescription,
        onNewSlice = { startNewSlice(runningSliceViewModel.currentDescription, Project(""), Task("")) },
        onCardClicked = finishedSlicesViewModel::updateChosenSlice,
        onSimilarSliceStarted = { id -> startSimilarSlice(id) },
        onCurrentSliceResumed = runningSliceViewModel::onResumeClicked,
        onCurrentSliceFinished = ::finishRunningSlice,
    )
}