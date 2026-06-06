package com.houvven.guise.constant

/**
 * Integer constants representing the device's network connectivity type.
 *
 * Used when spoofing [android.net.ConnectivityManager] results for target
 * applications so they observe a desired network state.
 */
object NetworkType {
    /** Cellular / mobile data connection. */
    const val MOBILE = 0

    /** Wi-Fi connection. */
    const val WIFI = 1

    /** No active network connection. */
    const val NONE = -1
}