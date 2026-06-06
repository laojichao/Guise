package com.houvven.guise.constant

import com.tencent.mmkv.MMKV

/**
 * Centralized registry of MMKV preference keys used throughout the application.
 *
 * Each constant defines a stable string key for persisting user settings and
 * internal configuration values via [MMKV]. A shared [mmkv] instance is provided
 * for convenience so callers do not need to manage their own reference.
 */
object AppConfigKey {

    /** Key for the sort strategy applied to the application list UI. */
    const val APP_SORT_TYPE = "app.sort.type"

    /** Key toggling whether the search bar filters by package name instead of app label. */
    const val APP_SEARCH_BY_PACKAGE_NAME = "app.search.by.package.name"

    /** Key toggling reverse (descending) sort order in the application list. */
    const val APP_REVERSE_SORT = "app.reverse.sort"

    /** Key toggling visibility of system (pre-installed) applications in the list. */
    const val DISPLAY_SYSTEM_APP = "display.system.app"

    /** Key storing the current version of the bundled device-info database. */
    const val DEVICE_DB_VERSION = "device.db.version"

    /** Key toggling whether the Xposed module remains active for all configured apps. */
    const val ALWAYS_ACTIVE = "always.active"

    /** Key toggling forced dark mode spoofing for target applications. */
    const val ALWAYS_DARK_MODE = "always.dark.mode"

    /** Key toggling elevated LSPosed compatibility mode. */
    const val SUPER_LSPOSED = "super.lsposed"

    /**
     * Shared MMKV instance opened in multi-process mode.
     *
     * Safe to use from any process (UI, Xposed hook, background service).
     */
    @JvmStatic
    val mmkv: MMKV  =  MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null)
}