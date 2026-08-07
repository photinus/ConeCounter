package com.conecounter.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.conecounter.app.data.Kid

fun parseKidColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: IllegalArgumentException) {
    Color(0xFF4FC3F7)
}

@Composable
fun KidAvatar(
    kid: Kid,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 56.dp,
    highlighted: Boolean = false
) {
    Box(
        modifier = modifier
            .size(avatarSize)
            .clip(CircleShape)
            .background(parseKidColor(kid.colorHex))
            .then(
                if (highlighted) {
                    Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = kid.emoji, fontSize = (avatarSize.value * 0.48f).sp)
    }
}
