package com.productivity.tracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = PrimaryLight.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryLight,
    secondary = SecondaryLight,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = SecondaryLight.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryVariantLight,
    tertiary = StreakGold,
    background = BackgroundLight,
    onBackground = androidx.compose.ui.graphics.Color.Black,
    surface = SurfaceLight,
    onSurface = androidx.compose.ui.graphics.Color.Black,
    error = ErrorLight,
    onError = androidx.compose.ui.graphics.Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = PrimaryDark.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryDark,
    secondary = SecondaryDark,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = SecondaryDark.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryDark,
    tertiary = StreakGold,
    background = BackgroundDark,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = SurfaceDark,
    onSurface = androidx.compose.ui.graphics.Color.White,
    error = ErrorDark,
    onError = androidx.compose.ui.graphics.Color.Black
)

@Composable
fun ProductivityTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
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
