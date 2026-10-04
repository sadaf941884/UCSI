package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = UcsiCrimsonLight,
    onPrimary = Color.White,
    primaryContainer = UcsiCrimsonDark,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFF8AB4F8),
    onSecondary = UcsiNavy,
    secondaryContainer = UcsiNavyLight,
    onSecondaryContainer = Color(0xFFD7E3FF),
    tertiary = UcsiGoldLight,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF5C4000),
    onTertiaryContainer = UcsiGoldContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = Color(0xFF222C3D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = UcsiCrimson,
    onPrimary = Color.White,
    primaryContainer = UcsiCrimsonContainer,
    onPrimaryContainer = UcsiOnCrimsonContainer,
    secondary = UcsiNavy,
    onSecondary = Color.White,
    secondaryContainer = UcsiNavyContainer,
    onSecondaryContainer = UcsiOnNavyContainer,
    tertiary = UcsiGold,
    onTertiary = Color.White,
    tertiaryContainer = UcsiGoldContainer,
    onTertiaryContainer = UcsiOnGoldContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = Color(0xFFE2E8F0),
    error = StatusError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
