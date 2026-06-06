package com.houvven.guise.xposed.config

/**
 * Defines constant values used across Xposed hook configurations.
 *
 * Each constant group represents a set of mutually exclusive options for a specific
 * hook feature (e.g., network type spoofing, screenshot policy). These values are
 * serialized into [ModuleConfig] and interpreted by the corresponding hook handlers.
 */
object HooksValue {

    // ── Network type constants ──────────────────────────────────────────────

    /** No network type override; the hook is inactive for network type. */
    const val NET_NONE = -1

    /** Disable network type spoofing; report the real network type. */
    const val NET_UNHOOK = 0

    /** Spoof the network type as Wi-Fi. */
    const val NET_WIFI = 1

    /** Spoof the network type as 2G mobile data. */
    const val NET_MOBILE_2G = 2

    /** Spoof the network type as 3G mobile data. */
    const val NET_MOBILE_3G = 3

    /** Spoof the network type as 4G (LTE) mobile data. */
    const val NET_MOBILE_4G = 4

    /** Spoof the network type as 5G mobile data. */
    const val NET_MOBILE_5G = 5

    // ── Screenshot flag constants ───────────────────────────────────────────

    /** No screenshot override; the hook is inactive for screenshot policy. */
    const val SCREENSHOTS_UNHOOK = -1

    /** Disable screenshots for the target application (FLAG_SECURE applied). */
    const val SCREENSHOTS_DISABLE = 0

    /** Enable screenshots for the target application (FLAG_SECURE removed). */
    const val SCREENSHOTS_ENABLE = 1
}