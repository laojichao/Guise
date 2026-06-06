package com.houvven.ktx_xposed

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * A safe wrapper around [SharedPreferences] that supports cross-process data sharing
 * between an Xposed module's host application and hooked target applications.
 *
 * When the Xposed module is activated ([HookStatus.isActivated] returns `true`), preferences
 * are opened with `MODE_WORLD_READABLE` so that hooked applications running in their own
 * processes can read configuration values written by the module's UI. When the module is
 * not activated, the behavior depends on the [strict] flag:
 *
 * - **Non-strict mode** (`strict = false`): Falls back to `MODE_PRIVATE`, which is
 *   suitable for the module's own UI process where cross-process reading is unnecessary.
 * - **Strict mode** (`strict = true`): Throws a [RuntimeException] because the caller
 *   explicitly requires cross-process access, which is impossible without the module being
 *   activated.
 *
 * Instances should be created via the companion object factory methods [of] and [ofStrict]
 * rather than calling the constructor directly.
 *
 * @property prefs the underlying [SharedPreferences] instance configured for the appropriate access mode.
 */
@SuppressLint("WorldReadableFiles")
class SafeSharePrefs private constructor(context: Context, name: String, strict: Boolean) {

    companion object {
        /**
         * Creates a non-strict [SharedPreferences] instance.
         *
         * If the Xposed module is activated, opens the preferences with `MODE_WORLD_READABLE`
         * for cross-process access. Otherwise, falls back to `MODE_PRIVATE`.
         *
         * @param context the [Context] used to access the shared preferences file.
         * @param name the name of the shared preferences file (without the `.xml` extension).
         * @return a [SharedPreferences] instance configured with the appropriate access mode.
         */
        @JvmStatic
        fun of(context: Context, name: String) = SafeSharePrefs(context, name, false).prefs

        /**
         * Creates a strict [SharedPreferences] instance that requires the Xposed module
         * to be activated.
         *
         * If the module is activated, opens the preferences with `MODE_WORLD_READABLE`.
         * If the module is not activated, throws a [RuntimeException] because strict mode
         * guarantees that cross-process reading is expected.
         *
         * @param context the [Context] used to access the shared preferences file.
         * @param name the name of the shared preferences file (without the `.xml` extension).
         * @return a [SharedPreferences] instance with `MODE_WORLD_READABLE` access.
         * @throws RuntimeException if the module is not activated (strict mode requires activation).
         */
        @JvmStatic
        fun ofStrict(context: Context, name: String) = SafeSharePrefs(context, name, true).prefs
    }

    var prefs: SharedPreferences

    init {
        prefs = when {
            // Module is active: use world-readable mode so hooked apps can read our prefs
            HookStatus.isActivated() -> context.getSharedPreferences(
                name, Context.MODE_WORLD_READABLE
            )

            // Module not active but non-strict: private mode is acceptable
            !strict -> context.getSharedPreferences(name, Context.MODE_PRIVATE)
            // Module not active and strict: this is a hard error
            else -> throw RuntimeException("This Mode's SafeSharePrefs is strict, but mode is not activated.")
        }
    }
}
