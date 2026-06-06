package com.houvven.guise.module.apps

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import com.houvven.guise.ContextAmbient
import com.houvven.guise.xposed.PackageConfig
import com.houvven.ktx_xposed.SafeSharePrefs

/**
 * Singleton provider that builds [AppInfo] instances from the device's installed packages.
 *
 * Reads the device's installed package list via [PackageManager] and cross-references
 * MMKV-backed shared preferences (via [SafeSharePrefs]) to determine which apps have
 * the Xposed module enabled.
 *
 * Annotated with [SuppressLint] because it intentionally holds the application context
 * rather than an Activity context.
 */
@SuppressLint("StaticFieldLeak")
object AppInfoProvider {

    /** The application context used for all package manager and shared preferences queries. */
    private val context = ContextAmbient.current

    /** The [PackageManager] instance obtained from the application context. */
    private val packageManager get() = context.packageManager

    /** The [SafeSharePrefs] instance that stores per-app module enable/disable state. */
    private val safeSharePrefs get() = SafeSharePrefs.of(context, PackageConfig.PREF_FILE_NAME)


    /**
     * Returns a list of [AppInfo] for all installed applications on the device.
     *
     * Iterates over every installed package and transforms each [PackageInfo] into an [AppInfo].
     *
     * @return an [ArrayList] containing one [AppInfo] per installed application.
     */
    fun getList(): ArrayList<AppInfo> {
        val list = arrayListOf<AppInfo>()
        this.getInstalledPackages().forEach { packageInfo ->
            list.add(generateAppInfo(packageInfo))
        }
        return list
    }

    /**
     * Returns a map of package names to [AppInfo] for all installed applications.
     *
     * Useful for O(1) lookup of app information by package name.
     *
     * @return a [HashMap] keyed by package name, with the corresponding [AppInfo] as the value.
     */
    fun getMap(): HashMap<String, AppInfo> {
        val map = hashMapOf<String, AppInfo>()
        this.getInstalledPackages().forEach { packageInfo ->
            map[packageInfo.packageName] = generateAppInfo(packageInfo)
        }
        return map
    }

    /**
     * Retrieves all installed packages using the appropriate API for the current Android version.
     *
     * Uses [PackageManager.PackageInfoFlags] on Android 13 (Tiramisu) and above,
     * and the legacy integer-flags overload on older versions.
     *
     * @return a list of [PackageInfo] for every installed package.
     */
    private fun getInstalledPackages(): List<PackageInfo> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0L))
        else packageManager.getInstalledPackages(0)


    /**
     * Constructs an [AppInfo] from the given [PackageInfo].
     *
     * Extracts the application label, icon, install/update timestamps, and system-app flag.
     * Checks [safeSharePrefs] to determine if the module is enabled for this package.
     *
     * @param packageInfo the [PackageInfo] to convert.
     * @return the populated [AppInfo] instance.
     */
    private fun generateAppInfo(packageInfo: PackageInfo): AppInfo {
        val applicationInfo = packageInfo.applicationInfo
        val packageName = applicationInfo.packageName

        // Check if this package has the Xposed module enabled in shared preferences
        val isEnable = safeSharePrefs.contains(packageName)
        val label = applicationInfo.loadLabel(packageManager).toString()
        val icon = applicationInfo.loadIcon(packageManager).toBitmap()
        val installTime = packageInfo.firstInstallTime
        val updateTime = packageInfo.lastUpdateTime
        val isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        return AppInfo(
            isEnable = isEnable,
            label = label,
            packageName = packageName,
            icon = icon,
            installTime = installTime,
            updateTime = updateTime,
            isSystemApp = isSystemApp
        )
    }


}