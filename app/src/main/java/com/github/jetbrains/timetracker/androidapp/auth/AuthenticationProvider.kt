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

    val currentUser: User?
}

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?
)