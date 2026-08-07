package com.conecounter.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape as ShapeRounded
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.conecounter.app.data.Kid
import com.conecounter.app.shortcuts.ShortcutHelper
import com.conecounter.app.ui.components.KidAvatar
import com.conecounter.app.ui.components.KidEditDialog

@Composable
fun KidsScreen(
    kids: List<Kid>,
    tripName: String,
    cruiseDay: Int,
    familyGoalOverride: Int?,
    autoFamilyGoal: Int,
    onAddKid: (name: String, emoji: String, colorHex: String, dailyGoal: Int) -> Unit,
    onUpdateKid: (Kid) -> Unit,
    onDeleteKid: (Kid) -> Unit,
    onTripNameChange: (String) -> Unit,
    onFamilyGoalOverrideChange: (Int?) -> Unit,
    onRestartTripToday: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showAddDialog by remember { mutableStateOf(false) }
    var editingKid by remember { mutableStateOf<Kid?>(null) }

    // Local, optimistic copies of the trip settings: the "real" values in `tripName` /
    // `familyGoalOverride` only update after a round trip through DataStore, which is too slow
    // to drive a text field or a rapid-tap stepper directly (typing/tapping would look laggy or
    // drop input while waiting for that echo). We seed from the incoming value once and then let
    // local edits win, firing the save on every change.
    var tripNameField by remember { mutableStateOf(tripName) }
    var goalOverrideField by remember { mutableStateOf(familyGoalOverride) }

    LazyColumn(
        // Keeps content (and the bottom nav bar, which lives outside this screen) clear of the
        // on-screen keyboard so it isn't left covering navigation after editing the trip name.
        modifier = Modifier.imePadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(shape = ShapeRounded(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Trip Setup", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Currently on Day $cruiseDay",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tripNameField,
                        onValueChange = {
                            tripNameField = it
                            onTripNameChange(it)
                        },
                        label = { Text("Trip name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = goalOverrideField != null,
                            onCheckedChange = { on ->
                                goalOverrideField = if (on) autoFamilyGoal else null
                                onFamilyGoalOverrideChange(goalOverrideField)
                            }
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Custom family goal")
                    }
                    val currentGoalOverride = goalOverrideField
                    if (currentGoalOverride != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                goalOverrideField = (currentGoalOverride - 1).coerceAtLeast(1)
                                onFamilyGoalOverrideChange(goalOverrideField)
                            }) { Icon(Icons.Filled.Remove, contentDescription = "Decrease") }
                            Text("$currentGoalOverride scoops/day")
                            IconButton(onClick = {
                                goalOverrideField = (currentGoalOverride + 1).coerceAtMost(99)
                                onFamilyGoalOverrideChange(goalOverrideField)
                            }) { Icon(Icons.Filled.Add, contentDescription = "Increase") }
                        }
                    } else {
                        Text(
                            "Auto goal: $autoFamilyGoal scoops/day (sum of each kid's goal)",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRestartTripToday) {
                        Text("Start Day 1 Today")
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kids", style = MaterialTheme.typography.titleLarge)
                Button(onClick = { showAddDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Add Kid")
                }
            }
        }

        if (kids.isEmpty()) {
            item {
                Text("No kids added yet. Tap \"Add Kid\" to get started!")
            }
        }

        items(kids, key = { it.id }) { kid ->
            Card(shape = ShapeRounded(20.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KidAvatar(kid = kid, avatarSize = 44.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(kid.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Goal: ${kid.dailyGoal} scoops/day",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    IconButton(onClick = {
                        ShortcutHelper.requestPinShortcut(context, kid)
                    }) {
                        Icon(Icons.Filled.Home, contentDescription = "Add home screen shortcut")
                    }
                    IconButton(onClick = { editingKid = kid }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onDeleteKid(kid) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        KidEditDialog(
            existing = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, emoji, color, goal ->
                onAddKid(name, emoji, color, goal)
                showAddDialog = false
                Toast.makeText(context, "Added $name! Tap the home icon to pin their shortcut.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    editingKid?.let { kid ->
        KidEditDialog(
            existing = kid,
            onDismiss = { editingKid = null },
            onSave = { name, emoji, color, goal ->
                onUpdateKid(kid.copy(name = name, emoji = emoji, colorHex = color, dailyGoal = goal))
                editingKid = null
            }
        )
    }
}
