package com.example.fretboardlearner.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define our dark color scheme using the colors from Color.kt
private val DarkColorScheme = darkColorScheme(
    primary = AccentColor,
    background = OledBlack,       // <-- THIS IS THE ONLY CHANGE
    surface = DarkGraySurface,    // Use the dark gray for surfaces
    onPrimary = Color.Black,      // Text on top of the accent color should be black for contrast
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    secondary = SecondaryText
)

@Composable
fun FretboardLearnerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // This will now be pure black
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}