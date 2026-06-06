package com.houvven.guise.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.houvven.guise.constant.AppConfigKey
import com.houvven.ktx_xposed.HookStatus

/**
 * Indicates whether the Xposed module hook is currently active.
 *
 * Returns `true` if the "always activate" preference is enabled or if the
 * LSPosed framework reports the module as activated via [HookStatus.isActivated].
 */
val isHooked: Boolean
    get() = alwaysActivate.value || HookStatus.isActivated()


/**
 * User preference for keeping the module hook permanently activated.
 *
 * Persisted in MMKV under the [AppConfigKey.ALWAYS_ACTIVE] key. When `true`,
 * the module behaves as if it is always hooked regardless of LSPosed scope.
 */
val alwaysActivate by derivedStateOf {
    mutableStateOf(AppConfigKey.run {
        mmkv.decodeBool(ALWAYS_ACTIVE, false)
    })
}

/**
 * User preference for forcing dark mode across the application.
 *
 * Persisted in MMKV under the [AppConfigKey.ALWAYS_DARK_MODE] key.
 * Always dark mode
 */
val alwaysDarkMode by derivedStateOf {
    mutableStateOf(AppConfigKey.run {
        mmkv.decodeBool(ALWAYS_DARK_MODE, false)
    })
}


/**
 * User preference for enabling the "super LSPosed" mode.
 *
 * When enabled, this mode extends the module's hooking capabilities beyond
 * the standard LSPosed scope. Persisted in MMKV under [AppConfigKey.SUPER_LSPOSED].
 */
val superLsposed by derivedStateOf {
    mutableStateOf(AppConfigKey.run {
        mmkv.decodeBool(SUPER_LSPOSED, false)
    })
}

/**
 * Transient UI state indicating that the user has requested to enable super LSPosed mode.
 *
 * Unlike [superLsposed], this is not persisted and resets on application restart.
 * Used to drive UI prompts (e.g., showing a confirmation or restart dialog).
 */
val requestSuperLsposed by derivedStateOf {
    mutableStateOf(false)
}
