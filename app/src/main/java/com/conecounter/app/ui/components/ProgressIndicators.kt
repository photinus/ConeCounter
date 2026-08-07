package com.conecounter.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.conecounter.app.ui.theme.CherryOrange
import com.conecounter.app.ui.theme.MintGreen
import com.conecounter.app.ui.theme.SunshineYellow

@Composable
fun ScoopProgressRing(
    count: Int,
    goal: Int,
    modifier: Modifier = Modifier,
    ringSize: Dp = 56.dp
) {
    val progress = if (goal <= 0) 1f else (count.toFloat() / goal).coerceIn(0f, 1f)
    val goalMet = goal > 0 && count >= goal
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = if (goalMet) MintGreen else CherryOrange

    Box(modifier = modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(ringSize)) {
            val strokeWidth = this.size.minDimension * 0.16f
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$count/$goal",
            fontSize = (ringSize.value * 0.24f).sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FamilyGoalBar(
    current: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (goal <= 0) 0f else (current.toFloat() / goal).coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0.02f, 1f))
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(listOf(SunshineYellow, CherryOrange))
                )
        )
    }
}
