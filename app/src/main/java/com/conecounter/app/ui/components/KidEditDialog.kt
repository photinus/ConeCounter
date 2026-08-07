package com.conecounter.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.conecounter.app.data.KID_AVATAR_COLORS
import com.conecounter.app.data.KID_AVATAR_EMOJIS
import com.conecounter.app.data.Kid

@Composable
fun KidEditDialog(
    existing: Kid?,
    onDismiss: () -> Unit,
    onSave: (name: String, emoji: String, colorHex: String, dailyGoal: Int) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var emoji by remember { mutableStateOf(existing?.emoji ?: KID_AVATAR_EMOJIS.first()) }
    var colorHex by remember { mutableStateOf(existing?.colorHex ?: KID_AVATAR_COLORS.first()) }
    var dailyGoal by remember { mutableStateOf(existing?.dailyGoal ?: 3) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add a Kid" else "Edit ${existing.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Text("Avatar", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KID_AVATAR_EMOJIS.forEach { option ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(parseKidColor(colorHex))
                                .then(
                                    if (option == emoji) {
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    } else Modifier
                                )
                                .clickable { emoji = option },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(option, fontSize = 20.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Color", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KID_AVATAR_COLORS.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(parseKidColor(hex))
                                .then(
                                    if (hex == colorHex) {
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    } else Modifier
                                )
                                .clickable { colorHex = hex }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Daily goal", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (dailyGoal > 1) dailyGoal-- }) {
                        Icon(Icons.Filled.Remove, contentDescription = "Decrease goal")
                    }
                    Text("$dailyGoal scoops/day", style = MaterialTheme.typography.bodyLarge)
                    IconButton(onClick = { if (dailyGoal < 20) dailyGoal++ }) {
                        Icon(Icons.Filled.Add, contentDescription = "Increase goal")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name.ifBlank { "Kid" }, emoji, colorHex, dailyGoal) }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
