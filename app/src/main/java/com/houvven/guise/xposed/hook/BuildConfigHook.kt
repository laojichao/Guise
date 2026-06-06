package com.houvven.guise.xposed.hook

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.afterHookAllMethods
import com.houvven.ktx_xposed.hook.findClassIfExists
import com.houvven.ktx_xposed.hook.lppram
import com.houvven.ktx_xposed.hook.setStaticField
import com.houvven.ktx_xposed.logger.XposedLogger

/**
 * Xposed hook that spoofs the target application's own version information.
 *
 * This hook operates on two fronts:
 * 1. **Runtime API interception** -- hooks [PackageManager.getPackageInfo] so that any
 *    runtime query for the target package returns the configured version name and code.
 * 2. **Static field patching** -- directly overwrites the `VERSION_CODE` and `VERSION_NAME`
 *    static fields on the target package's `BuildConfig` class, so code that reads these
 *    fields at compile time (inlined constants) also sees the spoofed values.
 *
 * If the target package does not contain a `BuildConfig` class (unusual but possible),
 * only the [PackageManager] hook is applied.
 */
class BuildConfigHook : LoadPackageHandler {

    /**
     * Installs hooks to spoof the target application's version code and version name.
     *
     * Steps:
     * 1. Attempts to locate `$packageName.BuildConfig` via the class loader.
     * 2. Hooks all overloads of [PackageManager.getPackageInfo] to return a fabricated
     *    [PackageInfo] with the configured version details.
     * 3. Overwrites the static `VERSION_CODE` / `VERSION_NAME` fields on the
     *    `BuildConfig` class when the corresponding config values are non-default.
     *
     * @see ModuleConfig.versionCode
     * @see ModuleConfig.versionName
     */
    override fun onHook() {
        val name = lppram.packageName
        val targetClass = findClassIfExists("$name.BuildConfig")

        if (targetClass == null) {
            XposedLogger.i("BuildConfigHook: $name.BuildConfig not found.")
            return
        }

        // Hook PackageManager.getPackageInfo to return spoofed version info
        // whenever the target package is queried.
        PackageManager::class.java.run {
            afterHookAllMethods("getPackageInfo") {
                if (it.args.contains(lppram.packageName)) {
                    it.result = PackageInfo().apply {
                        versionName = config.versionName
                        versionCode = config.versionCode
                        longVersionCode = config.versionCode.toLong()
                    }
                }
            }
        }

        // Directly patch BuildConfig static fields so that inlined constant reads
        // also see the spoofed values.
        if (config.versionCode != -1) {
            targetClass.setStaticField("VERSION_CODE", config.versionCode)
        }
        if (config.versionName.isNotBlank()) {
            targetClass.setStaticField("VERSION_NAME", config.versionName)
        }
    }
}