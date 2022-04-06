package com.github.jetbrains.timetracker.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.jetbrains.timetracker.androidapp.auth.FakeAuthenticationProvider
import com.github.jetbrains.timetracker.androidapp.auth.firebase.FirebaseAuthenticationProvider
import com.github.jetbrains.timetracker.androidapp.data.FakeSlicesRepository
import com.github.jetbrains.timetracker.androidapp.data.firestore.FirestoreSlicesRepository
import com.github.jetbrains.timetracker.androidapp.ui.TimeTrackerAppWithAuthentication
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO use dependency injection

        val useFirebase = true
        val useFirebaseEmulator = true

        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator
        // If run from Android device with 'Port forwarding', it should be "localhost"
        val host = "10.0.2.2"
//        val host = "localhost"

        val authProvider =
            if (useFirebase)
                FirebaseAuthenticationProvider(Firebase.auth)
            else
                FakeAuthenticationProvider()
        if (useFirebase && useFirebaseEmulator)
            Firebase.auth.useEmulator(host, 9099)

        val repository =
            if (useFirebase)
                FirestoreSlicesRepository(Firebase.firestore, authProvider)
            else
                FakeSlicesRepository()
        if (useFirebase && useFirebaseEmulator)
            Firebase.firestore.useEmulator(host, 8080)

        if (useFirebaseEmulator) {
            // To clear the old data instead of uninstalling the app
            Firebase.auth.signOut()
            Firebase.firestore.clearPersistence()
        }

        setContent {
            TimeTrackerAppWithAuthentication(authProvider, repository)
        }
    }
}