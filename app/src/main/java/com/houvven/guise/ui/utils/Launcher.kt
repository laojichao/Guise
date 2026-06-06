package com.houvven.guise.ui.utils

import android.content.ComponentName
import android.content.pm.PackageManager
import com.houvven.guise.ContextAmbient
import com.houvven.guise.ui.MainActivity

/**
 * Toggles the visibility of the application's launcher icon.
 *
 * This works by enabling or disabling the `MainActivityAlias` component declared
 * in the AndroidManifest. The app process is **not** killed when the state changes
 * ([PackageManager.DONT_KILL_APP]).
 *
 * @param flag `true` to hide the launcher icon, `false` to show it.
 */
fun hideLauncherIcon(flag: Boolean) {
    ContextAmbient.current.packageManager.setComponentEnabledSetting(
        ComponentName(ContextAmbient.current, MainActivity::class.java.name + "Alias"),
        if (!flag) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}

/**
 * Checks whether the application's launcher icon is currently hidden.
 *
 * @return `true` if the `MainActivityAlias` component is disabled (icon hidden),
 *         `false` otherwise.
 */
fun isHideLauncherIcon(): Boolean {
    return ContextAmbient.current.packageManager.getComponentEnabledSetting(
        ComponentName(ContextAmbient.current, MainActivity::class.java.name + "Alias")
    ) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
}
