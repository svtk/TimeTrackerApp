package com.github.jetbrains.timetracker.androidapp.ui

import androidx.compose.runtime.*
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
    if (authProvider.currentUser == null && showLoginScreen) {
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