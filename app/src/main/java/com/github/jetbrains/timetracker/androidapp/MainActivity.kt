package com.github.jetbrains.timetracker.androidapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.jetbrains.timetracker.androidapp.data.FakeSlicesRepository
import com.github.jetbrains.timetracker.androidapp.ui.TimeTrackerAppWithAuthentication
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO use dependency injection
        val repository = FakeSlicesRepository()

/*        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.
        val firestore = FirebaseFirestore.getInstance()
        firestore.useEmulator("10.0.2.2", 8080)

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        firestore.firestoreSettings = settings*/

        // TODO use emulator in debug mode
        // https://firebase.google.com/docs/emulator-suite/connect_auth
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        setContent {
            TimeTrackerAppWithAuthentication(repository)
        }
    }
}