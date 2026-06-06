package com.houvven.guise.ui.theme

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
import com.houvven.guise.constant.AppConfigKey
import com.houvven.guise.ui.alwaysDarkMode
import com.houvven.guise.xposed.PackageConfig


/** Predefined dark color scheme using purple, purple-grey, and pink accent colors. */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/** Predefined light color scheme using purple, purple-grey, and pink accent colors. */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)


/**
 * The root theme composable for the Guise application.
 *
 * Applies Material 3 theming with the following color scheme selection logic:
 * 1. If [alwaysDarkMode] is enabled (user preference), forces dark mode with
 *    dynamic colors on Android 12+ or the predefined [DarkColorScheme] on older versions
 * 2. If [dynamicColor] is enabled and the device runs Android 12+, uses Material You
 *    dynamic colors derived from the device wallpaper
 * 3. Falls back to [DarkColorScheme] or [LightColorScheme] based on system dark theme setting
 *
 * Also configures the status bar color to match the theme surface color and adjusts
 * the status bar icon appearance (light/dark) based on the effective dark mode state.
 *
 * @param darkTheme whether the system is currently in dark theme mode, defaults to [isSystemInDarkTheme]
 * @param dynamicColor whether to use Material You dynamic color on Android 12+, defaults to true
 * @param content the composable content tree to apply the theme to
 */
@Composable
fun GuiseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    var darkMode = darkTheme
    val colorScheme = when {
        alwaysDarkMode.value -> {
            darkMode = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicDarkColorScheme(LocalContext.current)
            else DarkColorScheme
        }

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
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkMode
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
