package com.houvven.guise.xposed.hook.netowork

import android.net.wifi.WifiInfo
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setMethodResult

/**
 * Xposed hook that spoofs Wi-Fi connection details reported by [WifiInfo].
 *
 * When the corresponding configuration values are non-blank, this hook
 * overrides the following methods:
 *
 * - [WifiInfo.getSSID] -- Returns the configured SSID wrapped in double quotes
 *   (as Android conventionally represents SSIDs).
 * - [WifiInfo.getBSSID] -- Returns the configured BSSID (access point MAC address).
 * - [WifiInfo.getMacAddress] -- Returns the configured device Wi-Fi MAC address.
 *
 * Each spoofing field is independently guarded by a non-blank check on the
 * corresponding [config] property, so only explicitly configured values are overridden.
 */
internal class WifiHook : LoadPackageHandler {
    override fun onHook() {
        WifiInfo::class.java.run {
            if (config.wifiSSID.isNotBlank()) setMethodResult("getSSID", "\"${config.wifiSSID}\"")
            if (config.wifiBSSID.isNotBlank()) setMethodResult("getBSSID", config.wifiBSSID)
            if (config.wifiMacAddress.isNotBlank()) setMethodResult("getMacAddress", config.wifiMacAddress)
        }
    }

}
