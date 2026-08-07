package com.conecounter.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.conecounter.app.data.Scoop
import com.conecounter.app.ui.ScoopWithKid
import com.conecounter.app.ui.components.KidAvatar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun LogScreen(
    scoops: List<ScoopWithKid>,
    onDelete: (Scoop) -> Unit
) {
    if (scoops.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No scoops logged yet. Tap \"Log a Scoop\" on the Home tab!")
        }
        return
    }

    val dateFormatter = remember(FormatStyle.MEDIUM) {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(scoops, key = { it.scoop.id }) { entry ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KidAvatar(kid = entry.kid, avatarSize = 40.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${entry.kid.name} · ${entry.scoop.flavor}", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = Instant.ofEpochMilli(entry.scoop.timestamp)
                                .atZone(ZoneId.systemDefault())
                                .format(dateFormatter),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = { onDelete(entry.scoop) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}
