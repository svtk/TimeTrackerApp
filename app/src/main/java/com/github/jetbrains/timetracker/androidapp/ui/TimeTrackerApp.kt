package com.github.jetbrains.timetracker.androidapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.jetbrains.timetracker.androidapp.auth.AuthenticationProvider
import com.github.jetbrains.timetracker.androidapp.ui.auth.LoginScreen
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.compose.get

@Composable
fun TimeTrackerAppWithAuthentication(
    authProvider: AuthenticationProvider = get()
) {
    var showLoginScreen by remember { mutableStateOf(true) }
    val auth: FirebaseAuth = Firebase.auth
    if (auth.currentUser == null && showLoginScreen) {
        var prevError by remember { mutableStateOf<String?>(null) }
        TimeTrackerAppTheme {
            LoginScreen(
                onLogIn = { email: String, password: String ->
                    authProvider.logIn(
                        email, password,
                        onSuccessfulLogIn = { showLoginScreen = false },
                        onError = { exception -> prevError = exception?.message },
                    )
                },
                onSignIn = { email: String, password: String, name: String ->
                    authProvider.signUp(
                        email, password, name,
                        onSuccessfulSignIn = { showLoginScreen = false },
                        onError = { exception -> prevError = exception?.message },
                    )
                },
                previousErrorText = prevError,
            )
        }
    } else {
        TimeTrackerApp()
    }
}

@Composable
fun TimeTrackerApp() {
    TimeTrackerAppTheme {
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        val currentScreen = TimeTrackerScreen.fromRoute(
            backstackEntry.value?.destination?.route
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    // TODO Show user details properly, add 'Log out'
                    title = { Text(currentScreen.title + " (${Firebase.auth.currentUser?.displayName ?: ""})") },
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
                TimeTrackerNavHost(navController)
            }
        }
    }
}