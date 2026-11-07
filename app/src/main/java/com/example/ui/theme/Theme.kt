package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SilentRunColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = AbyssDark,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = NeonCyan,
    secondary = WarningAmber,
    onSecondary = AbyssDark,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = WarningAmber,
    tertiary = BioGreen,
    onTertiary = AbyssDark,
    error = DangerCrimson,
    onError = TextPrimary,
    background = AbyssDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = OutlineBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = SilentRunColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AbyssDark.toArgb()
            window.navigationBarColor = AbyssDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
