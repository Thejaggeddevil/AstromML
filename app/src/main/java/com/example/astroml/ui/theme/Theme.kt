package com.example.astroml.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================
// LIGHT — Sage Green
// ============================================
private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = SurfaceLight,
    primaryContainer = SageGreenLight,
    onPrimaryContainer = TextPrimaryLight,
    secondary = SageGreenMedium,
    onSecondary = TextPrimaryLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SageGreenLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = ErrorColor,
)

// ============================================
// DARK — Soft Mocha
// ============================================
private val DarkColorScheme = darkColorScheme(
    primary = MochaBrown,
    onPrimary = TextPrimaryDark,
    primaryContainer = SurfaceDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = MochaBrownMedium,
    onSecondary = TextPrimaryDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    error = ErrorColor,
)

@Composable
fun AstroMLTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AstroTypography,
        content = content
    )
}