package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Forced dark cyberpunk scheme
private val CyberpunkColorScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = Color(0xFF020617),
    primaryContainer = Color(0x3300E5FF),
    onPrimaryContainer = Color(0xFFE2F8FF),
    secondary = CyberSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0x339123FF),
    onSecondaryContainer = Color(0xFFF3E8FF),
    tertiary = CyberTertiary,
    onTertiary = Color.White,
    background = CyberBg,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = Color(0xFFFF1744),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // We enforce our customized RPG game design theme as default
    MaterialTheme(
        colorScheme = CyberpunkColorScheme,
        typography = Typography,
        content = content
    )
}
