package com.github.jetbrains.timetracker.androidapp.ui

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.jetbrains.timetracker.androidapp.data.FakeSlicesRepository
import com.github.jetbrains.timetracker.androidapp.model.User
import com.github.jetbrains.timetracker.androidapp.ui.auth.LoginScreen
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun TimeTrackerAppWithAuthentication(
    repository: FakeSlicesRepository,
    onAuthenticationFail: () -> Unit
) {
    val auth: FirebaseAuth = Firebase.auth
    var currentUser = remember { auth.currentUser?.let { User(it.uid) } }
    if (currentUser == null) {
        LoginScreen(
            auth = auth,
            onLogin = { user -> currentUser = user },
            onFail = onAuthenticationFail
        )
    } else {
        TimeTrackerApp(repository)
    }
}

@Composable
fun TimeTrackerApp(repository: FakeSlicesRepository) {
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
                TimeTrackerNavHost(repository, navController)
            }
        }
    }
}