package com.productivity.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.productivity.tracker.ui.screens.*
import com.productivity.tracker.ui.theme.ProductivityTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductivityTrackerTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                Screen.items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Tasks.route) { TaskScreen() }
            composable(Screen.Habits.route) { HabitScreen() }
            composable(Screen.Skills.route) { SkillScreen() }
            composable(Screen.Steps.route) { StepCountScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
        }
    }
}

sealed class Screen(val route: String, val resourceId: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", R.string.nav_home, androidx.compose.material.icons.Icons.Default.Home)
    object Tasks : Screen("tasks", R.string.nav_tasks, androidx.compose.material.icons.Icons.Default.CheckCircle)
    object Habits : Screen("habits", R.string.nav_habits, androidx.compose.material.icons.Icons.Default.Repeat)
    object Skills : Screen("skills", R.string.nav_skills, androidx.compose.material.icons.Icons.Default.School)
    object Steps : Screen("steps", R.string.nav_steps, androidx.compose.material.icons.Icons.Default.DirectionsWalk)
    object Stats : Screen("stats", R.string.nav_stats, androidx.compose.material.icons.Icons.Default.BarChart)
    
    companion object {
        val items = listOf(Home, Tasks, Habits, Skills, Steps, Stats)
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    ProductivityTrackerTheme {
        MainApp()
    }
}
