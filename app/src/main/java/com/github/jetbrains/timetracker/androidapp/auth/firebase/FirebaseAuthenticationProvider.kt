package com.github.jetbrains.timetracker.androidapp.auth.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.github.jetbrains.timetracker.androidapp.auth.AuthenticationProvider
import com.github.jetbrains.timetracker.androidapp.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class FirebaseAuthenticationProvider(
    private val auth: FirebaseAuth = Firebase.auth,
) : AuthenticationProvider {
    override fun signUp(
        email: String,
        password: String,
        name: String,
        onSuccessfulSignIn: () -> Unit,
        onError: (e: Exception?) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")

                    updateDisplayName(name)
                    onSuccessfulSignIn()

                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onError(task.exception)
                }
            }
    }

    private fun updateDisplayName(name: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        auth.currentUser?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }
    }

    override fun logIn(
        email: String,
        password: String,
        onSuccessfulLogIn: () -> Unit,
        onError: (e: Exception?) -> Unit,
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    onSuccessfulLogIn()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onError(task.exception)
                }
            }
    }

    override val currentUser
        get() = auth.currentUser?.let {
            User(
                it.uid,
                it.email,
                it.displayName
            )
        }
}