package com.conecounter.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealDark,
    secondary = SunshineYellow,
    onSecondary = Color.Black,
    tertiary = CherryOrange,
    background = CreamBackground,
    onBackground = OnCreamText,
    surface = CardSurfaceLight,
    onSurface = OnCreamText,
    surfaceVariant = TealContainer,
)

private val DarkColors = darkColorScheme(
    primary = TealContainer,
    onPrimary = Color.Black,
    primaryContainer = TealDark,
    onPrimaryContainer = TealContainer,
    secondary = SunshineYellow,
    onSecondary = Color.Black,
    tertiary = CherryOrange,
    background = DeepSeaBackground,
    onBackground = OnDeepSeaText,
    surface = CardSurfaceDark,
    onSurface = OnDeepSeaText,
    surfaceVariant = CardSurfaceDark,
)

@Composable
fun ConeCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = ConeCounterTypography,
        content = content
    )
}
