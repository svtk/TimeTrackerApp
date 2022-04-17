package com.github.jetbrains.timetracker.androidapp

import android.app.Application
import com.github.jetbrains.timetracker.androidapp.auth.AuthenticationProvider
import com.github.jetbrains.timetracker.androidapp.auth.FakeAuthenticationProvider
import com.github.jetbrains.timetracker.androidapp.auth.firebase.FirebaseAuthenticationProvider
import com.github.jetbrains.timetracker.androidapp.data.FakeData
import com.github.jetbrains.timetracker.androidapp.data.FakeSlicesRepository
import com.github.jetbrains.timetracker.androidapp.data.SlicesRepository
import com.github.jetbrains.timetracker.androidapp.data.firestore.FirestoreSlicesRepository
import com.github.jetbrains.timetracker.androidapp.ui.home.LogsViewModel
import com.github.jetbrains.timetracker.androidapp.ui.home.TimerViewModel
import com.github.jetbrains.timetracker.androidapp.ui.slice.FinishedSliceViewModel
import com.github.jetbrains.timetracker.androidapp.ui.slice.RunningSliceViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val useFirebase = false

        val appModule = module {

            single<AuthenticationProvider> {
                if (useFirebase)
                    FirebaseAuthenticationProvider()
                else
                    FakeAuthenticationProvider()
            }
            single<FakeData> { FakeData }
            single<SlicesRepository> {
                if (useFirebase)
                    FirestoreSlicesRepository()
                else
                    FakeSlicesRepository(get())
            }
            if (useFirebase && BuildConfig.DEBUG) {
                setUpFirebaseEmulator()
            }

            viewModel { TimerViewModel(get()) }
            viewModel { RunningSliceViewModel(get()) }
            viewModel { LogsViewModel(get()) }
            viewModel { FinishedSliceViewModel(get()) }
        }

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)

            androidLogger(Level.ERROR)
            androidContext(this@MainApp)
            modules(appModule)
        }
    }

    private fun setUpFirebaseEmulator() {
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator
        // If run from Android device with 'Port forwarding', it should be 'localhost'
        val host = "10.0.2.2"
//        val host = "localhost"

        Firebase.auth.useEmulator(host, 9099)
        Firebase.firestore.useEmulator(host, 8080)

        // To clear the old data instead of uninstalling the app
//        Firebase.auth.signOut()
//        Firebase.firestore.clearPersistence()
    }
}

