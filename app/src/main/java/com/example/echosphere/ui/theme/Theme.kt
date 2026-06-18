package com.example.echosphere.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = EchoPrimary,
    onPrimary = EchoOnPrimary,
    background = EchoBackground,
    onBackground = EchoOnBackground,
    surface = EchoSurface,
    onSurface = EchoOnSurface,
    surfaceVariant = EchoSurfaceVariant
)

@Composable
fun EchosphereTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}