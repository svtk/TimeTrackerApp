package com.github.jetbrains.timetracker.androidapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.jetbrains.timetracker.androidapp.ui.home.HomeView
import com.github.jetbrains.timetracker.androidapp.ui.slice.FinishedSliceView
import com.github.jetbrains.timetracker.androidapp.ui.slice.RunningSliceView
import java.util.*

enum class TimeTrackerScreen(val title: String) {
    Home("Time Tracker"), RunningSlice("Running Time Slice"), ViewSlice("Edit Time Slice");

    companion object {
        fun fromRoute(route: String?): TimeTrackerScreen =
            when (route?.substringBefore("/")) {
                Home.name, null -> Home
                RunningSlice.name -> RunningSlice
                ViewSlice.name -> ViewSlice
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}

@Composable
fun TimeTrackerNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = TimeTrackerScreen.Home.name,
    ) {
        composable(TimeTrackerScreen.Home.name) {
            HomeView(
                navigateToRunningSlice = { navController.navigate(TimeTrackerScreen.RunningSlice.name) },
                navigateToChosenSlice = { id -> navController.navigate("${TimeTrackerScreen.ViewSlice.name}/$id") },
            )
        }
        composable(TimeTrackerScreen.RunningSlice.name) {
            RunningSliceView(
                navigateToHome = { navController.navigate(TimeTrackerScreen.Home.name) },
            )
        }
        composable(
            route = "${TimeTrackerScreen.ViewSlice.name}/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { entry ->
            val sliceId = UUID.fromString(entry.arguments?.get("id") as String)
            FinishedSliceView(
                id = sliceId,
            )
        }
    }
}