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

private val SasaDarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = TechDarkBackground,
    primaryContainer = CyanPrimaryContainer,
    secondary = IndigoSecondary,
    onSecondary = TextWhitePrimary,
    background = TechDarkBackground,
    onBackground = TextWhitePrimary,
    surface = TechDarkSurface,
    onSurface = TextWhitePrimary,
    surfaceVariant = TechDarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = TechDarkBorder,
    error = RoseError
)

@Composable
fun SasaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = SasaDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
