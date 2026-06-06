package com.houvven.guise.module.apps

import android.graphics.Bitmap

/**
 * Data model representing the essential information of an installed application.
 *
 * Used throughout the app for displaying application lists, filtering, sorting,
 * and determining which apps have the Xposed module enabled.
 *
 * @property isEnable whether the Xposed module is currently enabled for this application.
 * @property label the user-visible display name of the application.
 * @property packageName the unique Android package identifier (e.g., "com.example.app").
 * @property icon the application icon rendered as an [android.graphics.Bitmap].
 * @property installTime the timestamp (in milliseconds since epoch) when the app was first installed.
 * @property updateTime the timestamp (in milliseconds since epoch) when the app was last updated.
 * @property isSystemApp `true` if the app is a pre-installed system application, `false` otherwise.
 */
data class AppInfo(
    var isEnable: Boolean,
    val label: String,
    val packageName: String,
    val icon: Bitmap,
    val installTime: Long,
    val updateTime: Long,
    val isSystemApp: Boolean,
)
