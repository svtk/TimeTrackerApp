package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import com.example.timetrackerapp.data.FakeBlocksOfWorkRepository
import com.example.timetrackerapp.model.RunningBlockViewModel
import com.example.timetrackerapp.ui.FinishedBlocksViewModel
import com.example.timetrackerapp.ui.MainScreen
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO use dependency injection
        val repository = FakeBlocksOfWorkRepository()

        val runningBlockViewModel: RunningBlockViewModel by
            viewModels(factoryProducer = { RunningBlockViewModel.provideFactory(repository) })
        val finishedBlocksViewModel: FinishedBlocksViewModel by
            viewModels(factoryProducer = { FinishedBlocksViewModel.provideFactory(repository) })

        setContent {
            TimeTrackerAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Time Tracker") })
                    },
                ) {
                    Surface(color = MaterialTheme.colors.background) {
                        MainScreen(
                            runningBlockViewModel,
                            finishedBlocksViewModel,
                        )
                    }
                }
            }
        }
    }
}