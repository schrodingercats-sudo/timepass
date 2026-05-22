package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = ElectricBlue,
    secondary = ActiveRight,
    tertiary = ElectricPurple,
    background = AmoledBlack,
    surface = DarkSurface,
    surfaceVariant = BorderSurface,
    onPrimary = AmoledBlack,
    onSecondary = AmoledBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
