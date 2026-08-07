package com.conecounter.app.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.conecounter.app.MainActivity
import com.conecounter.app.shortcuts.ShortcutHelper
import com.conecounter.app.ui.components.LogScoopSheet
import com.conecounter.app.ui.screens.HomeScreen
import com.conecounter.app.ui.screens.KidsScreen
import com.conecounter.app.ui.screens.LogScreen
import com.conecounter.app.ui.screens.StatsScreen
import java.time.LocalDate

/**
 * The app has exactly 4 flat top-level tabs and no deep links or detail flows that need a real
 * back stack, so tab switching is done with plain state instead of Navigation-Compose — that
 * sidesteps a bottom-nav bug where popUpTo/saveState/restoreState occasionally landed on the
 * wrong tab after navigating in from a non-tab entry point (e.g. Home's "Add a Kid" button).
 */
private enum class Tab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Log("Log", Icons.Filled.List),
    Stats("Stats", Icons.Filled.BarChart),
    Kids("Kids", Icons.Filled.Group)
}

@Composable
fun AppRoot(
    viewModel: CounterViewModel,
    pendingAction: MainActivity.PendingAction?,
    onPendingActionHandled: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val kids = uiState.kidStats.map { it.kid }
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(Tab.Home) }
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
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                Tab.Home -> HomeScreen(
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
                    onGoToKids = { selectedTab = Tab.Kids }
                )
                Tab.Log -> LogScreen(scoops = uiState.recentScoops, onDelete = viewModel::deleteScoop)
                Tab.Stats -> StatsScreen(
                    totalScoopsAllTime = uiState.totalScoopsAllTime,
                    topFlavorAllTime = uiState.topFlavorAllTime,
                    weeklyCounts = uiState.weeklyCounts,
                    kidStats = uiState.kidStats
                )
                Tab.Kids -> KidsScreen(
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
