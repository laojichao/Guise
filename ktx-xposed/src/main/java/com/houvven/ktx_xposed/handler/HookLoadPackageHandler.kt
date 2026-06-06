package com.houvven.ktx_xposed.handler

import com.houvven.ktx_xposed.HookStatus
import com.houvven.ktx_xposed.LoadPackageHookAdapter
import com.houvven.ktx_xposed.hook.setLpparam
import com.houvven.ktx_xposed.hook.setMethodResult
import com.houvven.ktx_xposed.logger.XposedLogger
import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * Handler interface for Xposed modules that hook into the application load lifecycle.
 *
 * This interface extends [IXposedHookLoadPackage] and provides a structured framework
 * for processing `handleLoadPackage` callbacks. It distinguishes between two scenarios:
 *
 * 1. **Target package match**: When [lpparam] matches [packageName], the module is
 *    considered "inside its target application". The handler calls [doActivate] to mark
 *    the module as activated (making [HookStatus.isActivated] return `true`), signaling
 *    to other components that cross-process features are available.
 *
 * 2. **Non-target package**: When the loaded package does not match [packageName], the
 *    handler logs module identification info via [XposedLogger.doHookModuleLog] and
 *    delegates to [loadPackage] for custom per-package handling.
 *
 * Implementations should override [packageName] with the target application's package
 * name and implement [loadPackage] for any per-package hooking logic.
 *
 * ## Usage
 * ```kotlin
 * class MyModule : HookLoadPackageHandler {
 *     override val packageName = "com.example.target"
 *
 *     override fun loadPackage(lpparam: LoadPackageParam) {
 *         // Custom hooking logic for non-target packages
 *     }
 * }
 * ```
 */
interface HookLoadPackageHandler : IXposedHookLoadPackage {

    /**
     * The package name of the target application this module is designed to hook.
     *
     * When a package with this name is loaded, the module activates itself via [doActivate].
     * For all other packages, [loadPackage] is called instead.
     */
    val packageName: String

    /**
     * Main entry point called by the Xposed framework whenever a new package is loaded.
     *
     * This method initializes the global [LoadPackageParam] reference via [setLpparam]
     * and then routes execution based on whether the loaded package matches [packageName]:
     * - Match: calls [doActivate] to mark the module as active.
     * - No match: logs module info and delegates to [loadPackage].
     *
     * @param lpparam the [LoadPackageParam] provided by the Xposed framework containing
     *                information about the loaded package (package name, class loader, process name, etc.).
     */
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        setLpparam(lpparam)

        if (packageName == lpparam.packageName)
            doActivate()
        else {
            XposedLogger.doHookModuleLog()
            loadPackage(lpparam)
        }
    }

    /**
     * Called for packages that do not match [packageName].
     *
     * Implementations should override this method to perform any custom hooking logic
     * for non-target packages (e.g., hooking system framework classes that affect all apps).
     *
     * @param lpparam the [LoadPackageParam] provided by the Xposed framework for the loaded package.
     */
    fun loadPackage(lpparam: LoadPackageParam)

    /**
     * Executes a list of [LoadPackageHookAdapter] implementations with exception safety.
     *
     * Each adapter's [LoadPackageHookAdapter.onHook] method is invoked inside a
     * [runXposedCatching] block, ensuring that a failure in one adapter does not prevent
     * subsequent adapters from running. Errors are logged via [XposedLogger].
     *
     * @param hooks the list of [LoadPackageHookAdapter] instances to execute.
     */
    fun doHookLoadPackage(hooks: List<LoadPackageHookAdapter>) =
        hooks.forEach { runXposedCatching { it.onHook() } }

    /**
     * Activates the module by hooking [HookStatus.Companion.isActivated] to return `true`.
     *
     * This method uses [setMethodResult] to replace the return value of
     * [HookStatus.isActivated], which allows downstream components (e.g., [com.houvven.ktx_xposed.SafeSharePrefs])
     * to detect the activated state at runtime and enable cross-process features.
     *
     * This is automatically called by [handleLoadPackage] when the loaded package
     * matches [packageName].
     */
    fun doActivate() {
        val name = HookStatus.Companion::class.java.name
        setMethodResult(name, "isActivated", true)
    }
}
