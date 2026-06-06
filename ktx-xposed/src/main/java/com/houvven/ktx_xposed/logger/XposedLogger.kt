package com.houvven.ktx_xposed.logger

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.contentValuesOf
import com.houvven.ktx_xposed.hook.afterHookedMethod
import com.houvven.ktx_xposed.hook.lppram
import java.io.File


/**
 * Central logger singleton for Xposed modules.
 *
 * Collects log entries in-memory during hook execution and flushes them to
 * the [ModuleLogProvider] content provider when the host [Activity] pauses.
 * This lazy-flush strategy avoids database I/O on every log call while
 * ensuring all buffered entries are persisted before the activity becomes
 * invisible.
 *
 * ### Severity Levels
 * Log severity is represented as single characters defined in the nested
 * [Level] object:
 * - [Level.DEBUG] — `'D'`
 * - [Level.INFO]  — `'I'`
 * - [Level.ERROR] — `'E'`
 *
 * ### Lifecycle
 * 1. Call [doHookModuleLog] once during module initialisation to install
 *    the `Activity.onPause` hook.
 * 2. Use [d], [i], or [e] throughout the module to buffer log messages.
 * 3. On each `onPause`, the buffered entries are flushed to the content
 *    provider and the buffer is cleared.
 *
 * ### Content URI
 * Logs are sent to:
 * ```
 * content://com.houvven.xposed.runtime.log/module_log
 * ```
 */
@SuppressLint("StaticFieldLeak")
object XposedLogger {

    private const val TAG = "XposedLogger"

    /**
     * Single-character severity level constants used by [ModuleLog.type].
     */
    object Level {
        /** Debug-level log message. */
        const val DEBUG = 'D'
        /** Informational log message. */
        const val INFO = 'I'
        /** Error-level log message. */
        const val ERROR = 'E'
    }

    /**
     * In-memory buffer of log entries awaiting flush.
     *
     * Each entry is a [Pair] of `(level: Char, message: String)`.
     * The buffer is cleared after every successful flush in [doHookModuleLog].
     */
    private val logList = mutableListOf<Pair<Char, String>>()

    /** Content provider URI for the module log table. */
    private val uri = Uri.parse("content://com.houvven.xposed.runtime.log/module_log")

    /**
     * Buffers a debug-level log message.
     *
     * @param msg The message to log.
     */
    fun d(msg: String) {
        basicLog(Level.DEBUG, msg)
    }

    /**
     * Buffers an informational log message.
     *
     * @param msg The message to log.
     */
    fun i(msg: String) {
        basicLog(Level.INFO, msg)
    }

    /**
     * Buffers an error-level log message.
     *
     * @param msg The message to log.
     */
    fun e(msg: String) {
        basicLog(Level.ERROR, msg)
    }

    /**
     * Buffers an error-level log message derived from a [Throwable].
     *
     * The throwable's [Throwable.toString] representation is used as the message.
     *
     * @param throwable The exception or error to log.
     */
    fun e(throwable: Throwable) {
        basicLog(Level.ERROR, throwable.toString())
    }

    /**
     * Appends a log entry to the in-memory buffer.
     *
     * @param level The severity level character (e.g. [Level.DEBUG]).
     * @param msg   The log message body.
     */
    @SuppressLint("PrivateApi")
    private fun basicLog(level: Char, msg: String) {
        logList.add(level to msg)
    }

    /**
     * Installs an `afterHookedMethod` callback on [Activity.onPause] that
     * flushes all buffered log entries to the [ModuleLogProvider].
     *
     * **Must be called once** during module initialisation (typically in
     * `handleLoadPackage` or equivalent). When the hooked activity pauses:
     * 1. Each buffered entry is wrapped in [ContentValues] with fields
     *    `"type"`, `"source"`, and `"message"`.
     * 2. The values are inserted into the content provider.
     * 3. The in-memory buffer is cleared.
     *
     * Failures during flush are silently ignored to avoid crashing the host
     * application.
     */
    fun doHookModuleLog() {
        Activity::class.java.afterHookedMethod("onPause") { hookParam ->
            val application = hookParam.thisObject as Activity
            // Skip flush when there are no buffered entries.
            if (logList.isEmpty()) return@afterHookedMethod

            runCatching {
                logList.forEach { log ->
                    contentValuesOf(
                        "type" to log.first.toString(),
                        "source" to lppram.packageName,
                        "message" to log.second
                    ).let {
                        application.contentResolver.insert(uri, it)
                    }
                }
                logList.clear()
            }.onFailure {
                // Silently swallow — logging must never crash the host app.
            }
        }
    }


}
