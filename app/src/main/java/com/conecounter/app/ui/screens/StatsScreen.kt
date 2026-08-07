package com.conecounter.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.conecounter.app.ui.DayCount
import com.conecounter.app.ui.KidStats
import com.conecounter.app.ui.components.KidAvatar
import com.conecounter.app.ui.theme.CherryOrange
import com.conecounter.app.ui.theme.TealPrimary

@Composable
fun StatsScreen(
    totalScoopsAllTime: Int,
    topFlavorAllTime: String?,
    weeklyCounts: List<DayCount>,
    kidStats: List<KidStats>
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(label = "Cruise Total", value = "$totalScoopsAllTime", modifier = Modifier.weight(1f))
                StatTile(label = "Top Flavor", value = topFlavorAllTime ?: "—", modifier = Modifier.weight(1f))
            }
        }

        item {
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("This Week", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    WeeklyBarChart(weeklyCounts)
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("🏆 Leaderboard", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(12.dp))
                    val sorted = kidStats.sortedByDescending { it.scoopsTotal }
                    val maxTotal = sorted.maxOfOrNull { it.scoopsTotal }?.coerceAtLeast(1) ?: 1
                    if (sorted.isEmpty()) {
                        Text("No kids yet.")
                    }
                    sorted.forEachIndexed { index, stat ->
                        LeaderboardRow(rank = index + 1, stat = stat, maxTotal = maxTotal)
                        if (index != sorted.lastIndex) Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(20.dp), modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = TealPrimary)
        }
    }
}

@Composable
private fun WeeklyBarChart(days: List<DayCount>) {
    val maxCount = days.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEach { day ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${day.count}", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                val barHeight = (80.dp * (day.count.toFloat() / maxCount)).let { if (it < 4.dp) 4.dp else it }
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (day.isToday) CherryOrange else TealPrimary)
                )
                Spacer(Modifier.height(6.dp))
                Text(day.label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun LeaderboardRow(rank: Int, stat: KidStats, maxTotal: Int) {
    val medal = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "$rank."
    }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(medal, modifier = Modifier.width(28.dp))
        KidAvatar(kid = stat.kid, avatarSize = 32.dp)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(stat.kid.name, style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (stat.scoopsTotal.toFloat() / maxTotal).coerceIn(0.05f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(TealPrimary)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text("${stat.scoopsTotal}", style = MaterialTheme.typography.titleMedium)
    }
}
