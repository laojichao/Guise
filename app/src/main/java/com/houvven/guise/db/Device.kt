package com.houvven.guise.db

/**
 * Data model representing a single Android device profile read from the
 * bundled device-info SQLite database.
 *
 * Each instance describes a real-world device that can be used to spoof
 * [android.os.Build] fields in the Xposed module.
 *
 * @property brand      Internal brand identifier (e.g. `"samsung"`).
 * @property brandTitle  Human-readable brand name (e.g. `"Samsung"`).
 * @property code        Device codename (e.g. `"starlte"`).
 * @property codeAlias   Alternative codename, if available.
 * @property dtype       Device type classification (e.g. `"phone"`, `"tablet"`).
 * @property model       Model number (e.g. `"SM-G960F"`).
 * @property modelName   Market-facing model name (e.g. `"Galaxy S9"`).
 * @property verName     Android version name shipped with the device.
 */
data class Device(
    val brand: String?,
    val brandTitle: String?,
    val code: String?,
    val codeAlias: String?,
    val dtype: String?,
    val model: String?,
    val modelName: String?,
    val verName: String?
)