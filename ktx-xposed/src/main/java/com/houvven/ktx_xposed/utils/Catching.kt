package com.houvven.ktx_xposed.utils

import com.houvven.ktx_xposed.logger.XposedLogger

/**
 * Executes the given [block] inside a try-catch, logging any thrown [Throwable]
 * via [XposedLogger.e] and returning `null` on failure.
 *
 * This is the Xposed-aware counterpart of Kotlin's standard `runCatching`.
 * Instead of capturing the exception in a [Result], it forwards it directly
 * to the module's logging subsystem so that errors inside hook callbacks are
 * recorded in the persisted log database rather than silently swallowed or
 * crashing the host application.
 *
 * @param R    The return type of [block].
 * @param block The lambda to execute safely.
 * @return The result of [block] on success, or `null` if an exception was thrown.
 */
inline fun <R> runXposedCatching(block: () -> R): R? {
    return try {
        block()
    } catch (e: Throwable) {
        XposedLogger.e(e)
        null
    }
}