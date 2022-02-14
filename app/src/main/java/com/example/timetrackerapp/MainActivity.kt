package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import com.example.timetrackerapp.data.FakeSlicesRepository
import com.example.timetrackerapp.ui.RunningSliceViewModel
import com.example.timetrackerapp.ui.FinishedSlicesViewModel
import com.example.timetrackerapp.ui.MainScreen
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO use dependency injection
        val repository = FakeSlicesRepository()

        val runningSliceViewModel: RunningSliceViewModel by
            viewModels(factoryProducer = { RunningSliceViewModel.provideFactory(repository) })
        val finishedSlicesViewModel: FinishedSlicesViewModel by
            viewModels(factoryProducer = { FinishedSlicesViewModel.provideFactory(repository) })

        setContent {
            TimeTrackerAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Time Tracker") })
                    },
                ) {
                    Surface(color = MaterialTheme.colors.background) {
                        MainScreen(
                            runningSliceViewModel,
                            finishedSlicesViewModel,
                        )
                    }
                }
            }
        }
    }
}