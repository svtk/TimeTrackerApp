package com.example.timetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timetrackerapp.data.FakeSlicesRepository
import com.example.timetrackerapp.ui.Navigation
import com.example.timetrackerapp.ui.TimeTrackerScreen
import com.example.timetrackerapp.ui.theme.TimeTrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO use dependency injection
        val repository = FakeSlicesRepository()

        setContent {
            TimeTrackerAppTheme {
                val navController = rememberNavController()
                val backstackEntry = navController.currentBackStackEntryAsState()
                val currentScreen = TimeTrackerScreen.fromRoute(
                    backstackEntry.value?.destination?.route
                )
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(currentScreen.title) },
                            navigationIcon = if (currentScreen != TimeTrackerScreen.Home) {
                                {
                                    IconButton(onClick = { navController.navigate(TimeTrackerScreen.Home.name) }) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            } else {
                                null
                            },
                        )
                    },
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colors.background
                    ) {
                        Navigation(repository, navController)
                    }
                }
            }
        }
    }
}