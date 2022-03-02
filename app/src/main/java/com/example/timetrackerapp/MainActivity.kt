package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.timetrackerapp.data.FakeSlicesRepository
import com.example.timetrackerapp.ui.TimeTrackerApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO use dependency injection
        val repository = FakeSlicesRepository()

        setContent {
            TimeTrackerApp(repository)
        }
    }

}