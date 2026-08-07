package com.conecounter.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.conecounter.app.data.COMMON_FLAVORS
import com.conecounter.app.data.Kid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScoopSheet(
    kids: List<Kid>,
    initialKidId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (kidId: Long, flavor: String) -> Unit
) {
    var selectedKidId by remember { mutableStateOf(initialKidId ?: kids.firstOrNull()?.id) }
    var selectedFlavor by remember { mutableStateOf(COMMON_FLAVORS.first()) }
    var customFlavor by remember { mutableStateOf("") }
    var useCustom by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Icecream, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Log a Scoop",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(Modifier.height(20.dp))

            if (kids.isEmpty()) {
                Text("Add a kid first from the Kids tab!", style = MaterialTheme.typography.bodyLarge)
                return@Column
            }

            Text("Who's eating?", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                kids.forEach { kid ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedKidId = kid.id }
                    ) {
                        KidAvatar(
                            kid = kid,
                            avatarSize = 52.dp,
                            highlighted = kid.id == selectedKidId,
                            modifier = Modifier.padding(2.dp)
                        )
                        Text(kid.name, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            Text("Flavor", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                COMMON_FLAVORS.forEach { flavor ->
                    FilterChip(
                        selected = !useCustom && selectedFlavor == flavor,
                        onClick = {
                            useCustom = false
                            selectedFlavor = flavor
                        },
                        label = { Text(flavor) }
                    )
                }
                FilterChip(
                    selected = useCustom,
                    onClick = { useCustom = true },
                    label = { Text("Other…") }
                )
            }

            if (useCustom) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = customFlavor,
                    onValueChange = { customFlavor = it },
                    label = { Text("Flavor name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val kidId = selectedKidId ?: return@Button
                    val flavor = if (useCustom) customFlavor.ifBlank { "Other" } else selectedFlavor
                    onConfirm(kidId, flavor)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = selectedKidId != null
            ) {
                Text("Log It! 🍦", textAlign = TextAlign.Center)
            }
        }
    }
}
