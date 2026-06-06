package com.houvven.guise.xposed.hook.netowork

import android.net.NetworkInfo
import com.houvven.guise.constant.NetworkType
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.guise.xposed.config.HooksValue
import com.houvven.ktx_xposed.hook.setMethodResult

/**
 * Xposed hook that orchestrates network-related spoofing for the target application.
 *
 * This class serves as the top-level entry point for all network hooks. It:
 * 1. Spoofs the base network type reported by [NetworkInfo.getType] when
 *    [config.networkType] is set to a value other than [HooksValue.NET_UNHOOK].
 * 2. Delegates to [WifiHook] for Wi-Fi connection detail spoofing (SSID, BSSID, MAC).
 * 3. Delegates to [SimHook] for SIM card identity spoofing (operator, name, country).
 *
 * The network type mapping translates the hook configuration value into the
 * corresponding [NetworkType] constant:
 * - [HooksValue.NET_WIFI] maps to [NetworkType.WIFI]
 * - [HooksValue.NET_MOBILE_2G] through [HooksValue.NET_MOBILE_5G] map to [NetworkType.MOBILE]
 * - Any other value maps to [NetworkType.NONE]
 *
 * When a specific mobile generation is configured (2G-5G), the corresponding
 * [android.telephony.TelephonyManager] network type is also spoofed via [SimHook].
 */
internal class NetworkHook : LoadPackageHandler {

    /**
     * Entry point for network hook initialization.
     *
     * If [config.networkType] is not [HooksValue.NET_UNHOOK], hooks the base
     * network type. Always initializes [WifiHook] and [SimHook] for their
     * respective spoofing tasks.
     */
    override fun onHook() {
        if (config.networkType != HooksValue.NET_UNHOOK) this.hookNetworkType()
        listOf(WifiHook(), SimHook()).forEach { it.onHook() }
    }


    /**
     * Hooks both the base network type ([NetworkInfo.getType]) and, for
     * mobile network types, the detailed mobile generation via [SimHook].
     *
     * @param networkType the configured network type from [HooksValue].
     */
    private fun hookNetworkType() {
        val networkType = config.networkType
        this.hookBaseNetType(networkType)
        if (networkType != HooksValue.NET_WIFI) {
            SimHook().hookMobileType(networkType)
        }
    }

    /**
     * Maps the configured [HooksValue] network type constant to the
     * corresponding [NetworkType] integer and overrides
     * [NetworkInfo.getType] to return that value.
     *
     * @param type the configured network type constant from [HooksValue].
     */
    private fun hookBaseNetType(type: Int) {
        val t = when (type) {
            HooksValue.NET_WIFI -> NetworkType.WIFI
            HooksValue.NET_MOBILE_5G,
            HooksValue.NET_MOBILE_4G,
            HooksValue.NET_MOBILE_3G,
            HooksValue.NET_MOBILE_2G -> NetworkType.MOBILE

            else -> NetworkType.NONE
        }
        NetworkInfo::class.java.setMethodResult("getType", t)
    }

}
