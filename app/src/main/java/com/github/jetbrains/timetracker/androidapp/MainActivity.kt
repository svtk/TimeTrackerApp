package com.github.jetbrains.timetracker.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.jetbrains.timetracker.androidapp.data.FakeSlicesRepository
import com.github.jetbrains.timetracker.androidapp.ui.TimeTrackerApp

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