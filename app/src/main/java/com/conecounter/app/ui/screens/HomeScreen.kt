package com.conecounter.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.conecounter.app.ui.KidStats
import com.conecounter.app.ui.components.FamilyGoalBar
import com.conecounter.app.ui.components.KidAvatar
import com.conecounter.app.ui.components.ScoopProgressRing
import com.conecounter.app.ui.theme.CherryOrange

@Composable
fun HomeScreen(
    tripName: String,
    cruiseDay: Int,
    kidStats: List<KidStats>,
    familyScoopsToday: Int,
    familyGoal: Int,
    totalScoopsAllTime: Int,
    topFlavorAllTime: String?,
    onLogScoopClick: () -> Unit,
    onKidCardClick: (Long) -> Unit,
    onGoToKids: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "🍦 Cone Counter",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$tripName · Day $cruiseDay",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Family Ice Cream Goal", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Today's Scoops: $familyScoopsToday / $familyGoal",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(10.dp))
                    FamilyGoalBar(current = familyScoopsToday, goal = familyGoal)
                }
            }
        }

        if (kidStats.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No kids yet!", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Add your kids in the Kids tab to start counting scoops.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onGoToKids) { Text("Add a Kid") }
                    }
                }
            }
        } else {
            items(kidStats) { stat ->
                KidCard(stat = stat, onClick = { onKidCardClick(stat.kid.id) })
            }
        }

        item {
            Button(
                onClick = onLogScoopClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("LOG A SCOOP  🍦", fontWeight = FontWeight.Bold)
            }
        }

        item {
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Total Family Consumption", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Cruise Total: $totalScoopsAllTime Scoops")
                    Text("Top Flavor: ${topFlavorAllTime ?: "—"}")
                }
            }
        }
    }
}

@Composable
private fun KidCard(stat: KidStats, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KidAvatar(kid = stat.kid, avatarSize = 52.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(stat.kid.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${stat.scoopsToday} scoops today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CherryOrange
                )
                Text(
                    "Favorite: ${stat.favoriteFlavor ?: "—"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            ScoopProgressRing(count = stat.scoopsToday, goal = stat.kid.dailyGoal)
        }
    }
}
