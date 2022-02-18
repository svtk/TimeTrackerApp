package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import com.example.timetrackerapp.data.FakeSlicesRepository
import com.example.timetrackerapp.ui.Navigation
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO use dependency injection
        val repository = FakeSlicesRepository()

        setContent {
            TimeTrackerAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Time Tracker") })
                    },
                ) {
                    Surface(
                        modifier = Modifier.padding(it),
                        color = MaterialTheme.colors.background
                    ) {
                        Navigation(repository)
                    }
                }
            }
        }
    }
}