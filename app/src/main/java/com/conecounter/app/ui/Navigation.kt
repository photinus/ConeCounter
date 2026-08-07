package com.conecounter.app.ui

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.conecounter.app.MainActivity
import com.conecounter.app.shortcuts.ShortcutHelper
import com.conecounter.app.ui.components.LogScoopSheet
import com.conecounter.app.ui.screens.HomeScreen
import com.conecounter.app.ui.screens.KidsScreen
import com.conecounter.app.ui.screens.LogScreen
import com.conecounter.app.ui.screens.StatsScreen
import java.time.LocalDate

private sealed class BottomTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomTab("home", "Home", Icons.Filled.Home)
    object Log : BottomTab("log", "Log", Icons.Filled.List)
    object Stats : BottomTab("stats", "Stats", Icons.Filled.BarChart)
    object Kids : BottomTab("kids", "Kids", Icons.Filled.Group)
}

private val bottomTabs = listOf(BottomTab.Home, BottomTab.Log, BottomTab.Stats, BottomTab.Kids)

@Composable
fun AppRoot(
    viewModel: CounterViewModel,
    pendingAction: MainActivity.PendingAction?,
    onPendingActionHandled: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val kids = uiState.kidStats.map { it.kid }
    val context = LocalContext.current
    val navController = rememberNavController()

    var showLogSheet by remember { mutableStateOf(false) }
    var preselectedKidId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(kids) {
        ShortcutHelper.syncDynamicShortcuts(context, kids)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(pendingAction) {
        when (val action = pendingAction) {
            is MainActivity.PendingAction.QuickLog -> {
                val result = viewModel.quickLog(action.kidId)
                if (result != null) {
                    val (name, flavor) = result
                    Toast.makeText(context, "🍦 Logged $flavor for $name!", Toast.LENGTH_SHORT).show()
                }
                onPendingActionHandled()
            }
            is MainActivity.PendingAction.OpenPicker -> {
                preselectedKidId = null
                showLogSheet = true
                onPendingActionHandled()
            }
            null -> {}
        }
    }

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination
            NavigationBar {
                bottomTabs.forEach { tab ->
                    val selected = currentRoute?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomTab.Home.route) {
                HomeScreen(
                    tripName = uiState.tripName,
                    cruiseDay = uiState.cruiseDay,
                    kidStats = uiState.kidStats,
                    familyScoopsToday = uiState.familyScoopsToday,
                    familyGoal = uiState.familyGoal,
                    totalScoopsAllTime = uiState.totalScoopsAllTime,
                    topFlavorAllTime = uiState.topFlavorAllTime,
                    onLogScoopClick = {
                        preselectedKidId = null
                        showLogSheet = true
                    },
                    onKidCardClick = { kidId ->
                        preselectedKidId = kidId
                        showLogSheet = true
                    },
                    onGoToKids = {
                        navController.navigate(BottomTab.Kids.route)
                    }
                )
            }
            composable(BottomTab.Log.route) {
                LogScreen(scoops = uiState.recentScoops, onDelete = viewModel::deleteScoop)
            }
            composable(BottomTab.Stats.route) {
                StatsScreen(
                    totalScoopsAllTime = uiState.totalScoopsAllTime,
                    topFlavorAllTime = uiState.topFlavorAllTime,
                    weeklyCounts = uiState.weeklyCounts,
                    kidStats = uiState.kidStats
                )
            }
            composable(BottomTab.Kids.route) {
                KidsScreen(
                    kids = kids,
                    tripName = uiState.tripName,
                    cruiseDay = uiState.cruiseDay,
                    familyGoalOverride = uiState.familyGoalOverride,
                    autoFamilyGoal = kids.sumOf { it.dailyGoal },
                    onAddKid = viewModel::addKid,
                    onUpdateKid = viewModel::updateKid,
                    onDeleteKid = viewModel::deleteKid,
                    onTripNameChange = viewModel::setTripName,
                    onFamilyGoalOverrideChange = viewModel::setFamilyGoalOverride,
                    onRestartTripToday = { viewModel.setStartDate(LocalDate.now()) }
                )
            }
        }
    }

    if (showLogSheet) {
        LogScoopSheet(
            kids = kids,
            initialKidId = preselectedKidId,
            onDismiss = { showLogSheet = false },
            onConfirm = { kidId, flavor ->
                viewModel.logScoop(kidId, flavor)
                showLogSheet = false
            }
        )
    }
}
