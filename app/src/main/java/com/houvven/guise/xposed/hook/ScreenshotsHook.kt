package com.houvven.guise.xposed.hook

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.guise.xposed.config.HooksValue
import com.houvven.ktx_xposed.hook.afterHookSomeSameNameMethod
import com.houvven.ktx_xposed.hook.afterHookedMethod
import com.houvven.ktx_xposed.hook.callMethod

/**
 * Xposed hook that controls the screenshot capability for the target application.
 *
 * Supports three modes configured via [ModuleConfig.screenshotsFlag]:
 * - [HooksValue.SCREENSHOTS_UNHOOK] -- no-op; the hook is not installed.
 * - [HooksValue.SCREENSHOTS_DISABLE] -- forces [WindowManager.LayoutParams.FLAG_SECURE]
 *   on every activity window, preventing screenshots and screen recording.
 * - [HooksValue.SCREENSHOTS_ENABLE] -- strips [WindowManager.LayoutParams.FLAG_SECURE]
 *   whenever the target app tries to set it, allowing screenshots in apps that normally
 *   block them (e.g., banking, DRM-protected content).
 */
class ScreenshotsHook : LoadPackageHandler {

    /**
     * Reads the screenshot policy from [config] and installs the appropriate hook.
     *
     * - [HooksValue.SCREENSHOTS_UNHOOK]: returns immediately, no hooks installed.
     * - [HooksValue.SCREENSHOTS_DISABLE]: calls [disableScreenshots].
     * - [HooksValue.SCREENSHOTS_ENABLE]: calls [enableScreenshots].
     */
    override fun onHook() {
        if (config.screenshotsFlag == HooksValue.SCREENSHOTS_UNHOOK) return
        if (config.screenshotsFlag == HooksValue.SCREENSHOTS_DISABLE) disableScreenshots()
        else if (config.screenshotsFlag == HooksValue.SCREENSHOTS_ENABLE) enableScreenshots()
    }

    /**
     * Hooks [Activity.onCreate] to add [WindowManager.LayoutParams.FLAG_SECURE] to every
     * newly created activity window, effectively blocking screenshots and screen recording.
     */
    private fun disableScreenshots() {
        Activity::class.java.afterHookedMethod(
            methodName = "onCreate", Bundle::class.java
        ) {
            val activity = it.thisObject as Activity
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }

    /**
     * Hooks all window flag-setting methods ([Window.setFlags], [Window.addFlags], etc.)
     * so that whenever [WindowManager.LayoutParams.FLAG_SECURE] is set, it is immediately
     * cleared afterward via [Window.clearFlags]. This effectively neutralizes any attempt
     * by the target app to block screenshots.
     */
    private fun enableScreenshots() {
        /* Window::class.java.beforeHookSomeSameNameMethod(
            "setFlags",
            "setPrivateFlags",
            "addFlags",
            "addPrivateFlags",
            "addSystem",
            "addSystemFlags"
        ) {
            if (it.args[0] == WindowManager.LayoutParams.FLAG_SECURE) it.setNullResult()
        } */

        Window::class.java.afterHookSomeSameNameMethod(
            "setFlags",
            "setPrivateFlags",
            "addFlags",
            "addPrivateFlags",
            "addSystemFlags"
        ) {
            if (it.args[0] == WindowManager.LayoutParams.FLAG_SECURE)
                it.thisObject.callMethod("clearFlags", WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

}