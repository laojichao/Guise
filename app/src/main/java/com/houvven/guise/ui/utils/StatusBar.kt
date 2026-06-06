package com.houvven.guise.ui.utils

import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Composable that configures an immersive status bar by enabling edge-to-edge rendering
 * and applying the specified color to the system bars.
 *
 * This overload accepts an explicit [Window] reference and calls
 * `setDecorFitsSystemWindows(false)` to allow content to draw behind the status bar.
 * A [Spacer] matching the status bar height is rendered so that the composable content
 * is pushed below the inset area.
 *
 * Icons are automatically styled as dark or light depending on whether the system
 * is currently in dark theme mode.
 *
 * @param window the activity [Window] used to enable edge-to-edge rendering.
 * @param color the background [Color] to apply to the system bars. Defaults to
 *        [MaterialTheme.colorScheme.surface].
 */
@Composable
fun StatusBarImmerse(window: Window, color: Color = MaterialTheme.colorScheme.surface) {
    window.setDecorFitsSystemWindows(false)
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = color, darkIcons = !isSystemInDarkTheme())
    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars).fillMaxSize())
}

/**
 * Composable that applies a color to the system status and navigation bars
 * without modifying the window's decor-fits-system-windows setting.
 *
 * Use this overload when the host activity already handles edge-to-edge rendering
 * and only the system bar tint needs to be controlled.
 *
 * Icons are automatically styled as dark or light depending on whether the system
 * is currently in dark theme mode.
 *
 * @param color the background [Color] to apply to the system bars. Defaults to
 *        [MaterialTheme.colorScheme.surface].
 */
@Composable
fun StatusBarImmerse(color: Color = MaterialTheme.colorScheme.surface) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = color, darkIcons = !isSystemInDarkTheme())
}
