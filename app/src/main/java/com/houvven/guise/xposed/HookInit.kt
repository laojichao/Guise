package com.houvven.guise.xposed

import com.houvven.guise.BuildConfig
import com.houvven.guise.xposed.config.ModuleConfig
import com.houvven.guise.xposed.hook.BatteryHook
import com.houvven.guise.xposed.hook.BuildConfigHook
import com.houvven.guise.xposed.hook.LocalHook
import com.houvven.guise.xposed.hook.OsBuildHook
import com.houvven.guise.xposed.hook.ScreenshotsHook
import com.houvven.guise.xposed.hook.UniquelyIdHook
import com.houvven.guise.xposed.hook.location.CellLocationHook
import com.houvven.guise.xposed.hook.location.LocationHook
import com.houvven.guise.xposed.hook.netowork.NetworkHook
import com.houvven.guise.xposed.other.BlankPass
import com.houvven.guise.xposed.other.HookSuccessHint
import com.houvven.ktx_xposed.handler.HookLoadPackageHandler
import com.houvven.ktx_xposed.logger.XposedLogger
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Main entry point for the Xposed module's package loading lifecycle.
 *
 * This class implements [HookLoadPackageHandler] and serves as the bootstrap that initializes
 * all individual hook components when a target application is loaded. It checks whether the
 * current package has an active configuration via [PackageConfig], and if enabled, delegates
 * to a chain of hook handlers covering device identity, network, location, battery, and more.
 *
 * The hook chain includes:
 * - [HookSuccessHint] -- Visual indicator that hooks were applied
 * - [BatteryHook] -- Battery level spoofing
 * - [LocalHook] -- Locale/language spoofing
 * - [LocationHook] -- GPS/geolocation spoofing
 * - [CellLocationHook] -- Cellular tower location spoofing
 * - [NetworkHook] -- Network type spoofing
 * - [OsBuildHook] -- Android OS build properties spoofing
 * - [ScreenshotsHook] -- Screenshot policy manipulation
 * - [UniquelyIdHook] -- Device unique identifiers spoofing
 * - [BlankPass] -- Pass-through handler for excluded content types
 * - [BuildConfigHook] -- Target app build config spoofing
 */
@Suppress("unused")
class HookInit : HookLoadPackageHandler {

    /**
     * The current module configuration for the loaded package.
     * Lazily retrieved from [PackageConfig.current] on each access.
     */
    private val packageConfig: ModuleConfig
        get() = PackageConfig.current

    /**
     * The module's own package name, used to identify this Xposed module.
     */
    override val packageName = BuildConfig.APPLICATION_ID

    /**
     * Called by the Xposed framework when any application package is loaded.
     *
     * Refreshes the per-package configuration from shared preferences, checks whether
     * the hook is enabled for this package, and if so, instantiates and applies all
     * hook components via [doHookLoadPackage].
     *
     * @param lpparam the load-package parameters provided by the Xposed framework,
     *                containing the target package name and application info
     */
    override fun loadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedLogger.i("start loadPackage: ${lpparam.packageName} [${lpparam.appInfo.name}]")
        // Refresh configuration for the currently loading package
        PackageConfig.doRefresh(lpparam.packageName)
        if (!packageConfig.isEnable) {
            XposedLogger.i("loadPackage: ${lpparam.packageName} is not enable, skip.")
            return
        }

        listOf(
            HookSuccessHint(),
            BatteryHook(),
            LocalHook(),
            LocationHook(),
            CellLocationHook(),
            NetworkHook(),
            OsBuildHook(),
            ScreenshotsHook(),
            UniquelyIdHook(),
            BlankPass(),
            BuildConfigHook()
        ).let { doHookLoadPackage(it) }
    }

}