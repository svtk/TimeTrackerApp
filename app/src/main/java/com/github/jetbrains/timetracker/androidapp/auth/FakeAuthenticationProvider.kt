package com.github.jetbrains.timetracker.androidapp.auth

private const val FAKE_USER_ID = "f796d150-b4e8-11ec-b909-0242ac120002"

class FakeAuthenticationProvider: AuthenticationProvider {
    override fun signUp(
        email: String,
        password: String,
        name: String,
        onSuccessfulSignIn: () -> Unit,
        onError: (e: Exception?) -> Unit
    ) {
        // skipped in fake authentication
    }

    override fun logIn(
        email: String,
        password: String,
        onSuccessfulLogIn: () -> Unit,
        onError: (e: Exception?) -> Unit
    ) {
        // skipped in fake authentication
    }

    override val currentUser: User =
        User(FAKE_USER_ID, "test@test.com", "Test User")
}