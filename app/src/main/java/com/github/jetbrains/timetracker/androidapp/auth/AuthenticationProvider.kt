package com.github.jetbrains.timetracker.androidapp.auth

interface AuthenticationProvider {
    fun signUp(
        email: String,
        password: String,
        name: String,
        onSuccessfulSignIn: () -> Unit,
        onError: (e: Exception?) -> Unit,
    )

    fun logIn(
        email: String,
        password: String,
        onSuccessfulLogIn: () -> Unit,
        onError: (e: Exception?) -> Unit,
    )
}