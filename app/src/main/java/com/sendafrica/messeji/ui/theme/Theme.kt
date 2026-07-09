package com.sendafrica.messeji.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCEE9DD),
    onPrimaryContainer = Color(0xFF043A28),
    secondary = Color(0xFF4C6359),
    onSecondary = Color.White,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = CardLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFDAE5DE),
    onSurfaceVariant = TextSecondaryLight,
    outline = DividerLight,
    error = Alert,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF52C8A0),
    onPrimary = Color(0xFF003B27),
    primaryContainer = Color(0xFF00523B),
    onPrimaryContainer = Color(0xFFCEE9DD),
    secondary = Color(0xFFB3CCBF),
    onSecondary = Color(0xFF1F352B),
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = CardDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFF414E47),
    onSurfaceVariant = TextSecondaryDark,
    outline = DividerDark,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun MessejiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
