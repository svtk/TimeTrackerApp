package com.example.timetrackerapp.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timetrackerapp.data.SlicesRepository
import com.example.timetrackerapp.ui.home.HomeView
import com.example.timetrackerapp.ui.home.HomeViewModel
import com.example.timetrackerapp.ui.slice.FinishedSliceView
import com.example.timetrackerapp.ui.slice.FinishedSliceViewModel
import com.example.timetrackerapp.ui.slice.RunningSliceView
import com.example.timetrackerapp.ui.slice.RunningSliceViewModel
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
    repository: SlicesRepository,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = TimeTrackerScreen.Home.name,
    ) {
        composable(TimeTrackerScreen.Home.name) {
            HomeView(
                homeViewModel = viewModel(factory = HomeViewModel.provideFactory(repository)),
                navigateToRunningSlice = { navController.navigate(TimeTrackerScreen.RunningSlice.name) },
                navigateToChosenSlice = { id -> navController.navigate("${TimeTrackerScreen.ViewSlice.name}/$id") },
            )
        }
        composable(TimeTrackerScreen.RunningSlice.name) {
            RunningSliceView(
                runningSliceViewModel = viewModel(
                    factory = RunningSliceViewModel.provideFactory(
                        repository
                    )
                ),
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
                finishedSliceViewModel = viewModel(
                    factory = FinishedSliceViewModel.provideFactory(
                        repository
                    )
                ),
            )
        }
    }
}