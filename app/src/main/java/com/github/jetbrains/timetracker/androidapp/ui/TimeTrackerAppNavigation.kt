package com.github.jetbrains.timetracker.androidapp.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Subject
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.jetbrains.timetracker.androidapp.ui.home.LogsView
import com.github.jetbrains.timetracker.androidapp.ui.slice.FinishedSliceView
import com.github.jetbrains.timetracker.androidapp.ui.slice.RunningSliceView
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

enum class TimeTrackerScreen(
    val title: String,
    val icon: ImageVector? = null,
) {
    Timer(
        title = "Timer",
        icon = Icons.Filled.MoreTime
    ),
    Logs(
        title = "Logs",
        icon = Icons.Filled.Subject
    ),
    Account(
        title = "Account",
        icon = Icons.Filled.AccountCircle
    ),
    // TODO correct navigation graph
    // made 'Slice' a subitem of 'Logs'
    Slice(
        title = "Edit Entry",
    );

    fun isMenuItem() = icon != null

    companion object {
        fun fromRoute(route: String?): TimeTrackerScreen =
            when (route?.substringBefore("/")) {
                Timer.name, null -> Timer
                Logs.name -> Logs
                Account.name -> Account
                Slice.name -> Slice
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: TimeTrackerScreen,
    navController: NavHostController,
) {
    @Composable
    fun RowScope.TimeTrackerBottomNavigationItem(
        item: TimeTrackerScreen,
    ) {
        checkNotNull(item.icon) { "Wrong navigation item $item" }
        BottomNavigationItem(
            icon = { Icon(item.icon, contentDescription = item.title) },
            label = { Text(text = item.title) },
            selectedContentColor = MaterialTheme.colors.onPrimary,
            unselectedContentColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.7f),
            alwaysShowLabel = true,
            selected = currentScreen == item,
            onClick = { navController.navigate(item.name) },
        )
    }

    BottomNavigation {
        TimeTrackerBottomNavigationItem(
            item = TimeTrackerScreen.Timer,
        )
        TimeTrackerBottomNavigationItem(
            item = TimeTrackerScreen.Logs,
        )
        TimeTrackerBottomNavigationItem(
            item = TimeTrackerScreen.Account,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    TimeTrackerAppTheme {
        BottomNavigationBar(TimeTrackerScreen.Timer, rememberNavController())
    }
}

@Composable
fun TimeTrackerApp() {
    TimeTrackerAppTheme {
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        val currentScreen = TimeTrackerScreen.fromRoute(
            backstackEntry.value?.destination?.route
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    navigationIcon = if (!currentScreen.isMenuItem()) {
                        {
                            // TODO correct navigation graph
                            val backRoute = TimeTrackerScreen.Logs.name
                            IconButton(onClick = { navController.navigate(backRoute) }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    } else {
                        null
                    },
                )
            },
            bottomBar = {
                if (currentScreen.isMenuItem()) {
                    BottomNavigationBar(currentScreen, navController)
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colors.background
            ) {
                TimeTrackerNavHost(navController)
            }
        }
    }
}

@Composable
fun TimeTrackerNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = TimeTrackerScreen.Timer.name,
    ) {
        composable(TimeTrackerScreen.Logs.name) {
            LogsView(
                navigateToRunningSlice = { navController.navigate(TimeTrackerScreen.Timer.name) },
                navigateToChosenSlice = { id -> navController.navigate("${TimeTrackerScreen.Slice.name}/$id") },
            )
        }
        composable(TimeTrackerScreen.Timer.name) {
            RunningSliceView(
                navigateToRunningSlice = { navController.navigate(TimeTrackerScreen.Timer.name) },
                navigateToLogs = { navController.navigate(TimeTrackerScreen.Logs.name) },
            )
        }
        composable(TimeTrackerScreen.Account.name) {
            Text(Firebase.auth.currentUser?.displayName ?: "Error: no account")
        }
        composable(
            route = "${TimeTrackerScreen.Slice.name}/{id}",
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