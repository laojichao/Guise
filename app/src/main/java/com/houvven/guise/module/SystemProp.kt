package com.houvven.guise.module

import com.houvven.lib.command.ShellActuators

/**
 * Provides read-only access to device system properties relevant to CPU architecture.
 *
 * All properties are queried at runtime via the `getprop` shell command, making this
 * object suitable for use in environments where standard Android APIs may not reflect
 * the actual device state (e.g., inside an Xposed module context).
 */
object SystemProp {

    /**
     * The raw ABI string for the device's primary CPU (e.g., "arm64-v8a", "x86_64").
     *
     * Executes `getprop ro.product.cpu.abi` via a shell command and trims the result.
     * Returns "unknown" if the command fails or the result is empty.
     */
    @JvmStatic
    val abi: String
        get() {
            val abi = ShellActuators.exec("getprop ro.product.cpu.abi", true)
            return if (abi.isSuccess) abi.getOrNull()?.trim() ?: "unknown" else "unknown"
        }


    /**
     * A normalized, shorthand architecture string derived from [abi].
     *
     * Maps raw ABI values to simplified identifiers:
     * - "arm64-v8a" -> "arm64"
     * - "armeabi-v7a" -> "arm"
     * - "x86_64" -> "x86_64"
     * - "x86" -> "x86"
     * - anything else -> "unknown"
     */
    @JvmStatic
    val architecture: String
        get() = when (abi) {
            "arm64-v8a" -> "arm64"
            "armeabi-v7a" -> "arm"
            "x86_64" -> "x86_64"
            "x86" -> "x86"
            else -> "unknown"
        }

}