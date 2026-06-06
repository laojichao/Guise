package com.houvven.guise.xposed

import android.content.SharedPreferences
import com.houvven.guise.BuildConfig
import com.houvven.guise.ContextAmbient
import com.houvven.guise.xposed.config.ModuleConfig
import com.houvven.ktx_xposed.SafeSharePrefs
import de.robv.android.xposed.XSharedPreferences

/**
 * Singleton responsible for loading and caching the [ModuleConfig] for the currently
 * loaded target package at Xposed hook time.
 *
 * Configuration is persisted in a SharedPreferences file named [PREF_FILE_NAME] and
 * shared between the module's UI process (via [safePrefs]) and the Xposed hook process
 * (via [xSharedPrefs], which uses [XSharedPreferences] for cross-process access).
 *
 * The [current] property holds the active configuration and must be refreshed via
 * [doRefresh] before each package's hooks are applied.
 */
object PackageConfig {

    /**
     * The active [ModuleConfig] for the package currently being loaded by the Xposed framework.
     *
     * This property is set by [doRefresh] and consumed by hook handlers via
     * [LoadPackageHandler.config]. Must not be accessed before [doRefresh] is called.
     *
     * @throws UninitializedPropertyAccessException if accessed before [doRefresh] is called
     */
    lateinit var current: ModuleConfig

    /**
     * The SharedPreferences file name used to store per-package hook configurations.
     */
    const val PREF_FILE_NAME = "XposedDeployInfo"

    /**
     * A process-safe [SharedPreferences] instance for the module's own application process.
     *
     * Used by the UI layer to read and write configuration. Backed by [SafeSharePrefs]
     * which handles cross-process read safety.
     */
    val safePrefs: SharedPreferences
        get() = SafeSharePrefs.of(ContextAmbient.current, PREF_FILE_NAME)

    /**
     * A cross-process [XSharedPreferences] instance for reading configuration from
     * within the Xposed hook process.
     *
     * Initialized lazily and made world-readable so that hooked applications can
     * access the stored preferences.
     */
    val xSharedPrefs by lazy {
        XSharedPreferences(BuildConfig.APPLICATION_ID, PREF_FILE_NAME).also {
            it.makeWorldReadable()
        }
    }

    /**
     * Refreshes [current] with the configuration for the specified [packageName].
     *
     * Reads the JSON-serialized [ModuleConfig] from [xSharedPrefs]. If no configuration
     * exists for the given package, a default (disabled) [ModuleConfig] is used.
     * The [ModuleConfig.packageName] field is always set to [packageName] regardless.
     *
     * @param packageName the Android package name of the application being loaded
     */
    fun doRefresh(packageName: String) {
        // Check whether a configuration entry exists for this package
        val b = xSharedPrefs.contains(packageName)
        current = when {
            b -> ModuleConfig.fromJson(xSharedPrefs.getString(packageName, "")!!)
            else -> ModuleConfig()
        }

        current.packageName = packageName
    }

}