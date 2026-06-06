package com.houvven.guise.xposed.hook.location

import android.location.Criteria
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.UserHandle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.INCLUDE_LOCATION_DATA_NONE
import android.telephony.gsm.GsmCellLocation
import com.houvven.ktx_xposed.hook.beforeHookSomeSameNameMethod
import com.houvven.ktx_xposed.hook.setMethodResult
import com.houvven.ktx_xposed.hook.setSomeSameNameMethodResult

/**
 * Base class providing reusable hook utilities for disabling non-GPS location
 * providers so that a spoofed GPS location cannot be cross-checked against
 * alternative positioning sources.
 *
 * Subclasses (e.g., [LocationHook]) inherit these helpers and can selectively
 * call them based on configuration flags.
 *
 * The utilities are organized into four groups:
 * 1. **Provider state** -- Forces [LocationManager] to report GPS as the only
 *    available and best provider, while disabling NETWORK, FUSED, and PASSIVE.
 * 2. **Telephony location** -- Nullifies cell-based location data from
 *    [TelephonyManager] and [PhoneStateListener].
 * 3. **Wi-Fi location** -- Disables Wi-Fi scanning and spoofs MAC addresses
 *    to zero, preventing Wi-Fi-based positioning.
 * 4. **Cell location** -- Invalidates [GsmCellLocation] identifiers (PSC, LAC)
 *    so cell-tower triangulation fails.
 */
@Suppress("DEPRECATION")
open class LocationHookBase {

    /**
     * Disables all non-GPS location providers by combining provider state
     * manipulation and telephony location nullification.
     *
     * Should be called after the primary GPS location hook is installed.
     */
    protected fun setOtherServicesFail() {
        setProviderState()
        setTelLocationFail()
    }

    /**
     * Configures [LocationManager] to report GPS as the sole available and
     * best provider.
     *
     * Hooks the following methods:
     * - `isLocationEnabledForUser` -- returns `true` so location appears enabled.
     * - `isProviderEnabledForUser` / `hasProvider` -- returns `true` for GPS,
     *   `false` for FUSED, NETWORK, and PASSIVE providers.
     * - `getProviders` / `getAllProviders` -- returns a list containing only GPS.
     * - `getBestProvider` -- returns [LocationManager.GPS_PROVIDER].
     */
    private fun setProviderState() {
        LocationManager::class.java.apply {
            setMethodResult(
                methodName = "isLocationEnabledForUser",
                value = true,
                parameterTypes = arrayOf(UserHandle::class.java)
            )
            beforeHookSomeSameNameMethod(
                "isProviderEnabledForUser", "hasProvider"
            ) {
                when (it.args[0] as String) {
                    LocationManager.GPS_PROVIDER -> it.result = true
                    LocationManager.FUSED_PROVIDER,
                    LocationManager.NETWORK_PROVIDER,
                    LocationManager.PASSIVE_PROVIDER,
                    -> it.result = false
                }
            }
            setSomeSameNameMethodResult(
                "getProviders", "getAllProviders",
                value = listOf(LocationManager.GPS_PROVIDER)
            )
            setMethodResult(
                methodName = "getBestProvider",
                value = LocationManager.GPS_PROVIDER,
                parameterTypes = arrayOf(Criteria::class.java, Boolean::class.java)
            )
        }
    }


    /**
     * Nullifies cell-tower-based location data from [TelephonyManager] and
     * [PhoneStateListener] to prevent the target app from obtaining location
     * via cellular infrastructure.
     *
     * Hooks the following:
     * - `getCellLocation`, `getAllCellInfo`, `getNeighboringCellInfo`,
     *   `getLastKnownCellIdentity` -- all return `null`.
     * - `getLocationData` (API 33+) -- returns [INCLUDE_LOCATION_DATA_NONE].
     * - `PhoneStateListener.onCellLocationChanged`,
     *   `PhoneStateListener.onCellInfoChanged` -- both return `null`.
     */
    private fun setTelLocationFail() {
        TelephonyManager::class.java.run {
            setSomeSameNameMethodResult(
                "getCellLocation",
                "getAllCellInfo",
                "getNeighboringCellInfo",
                "getLastKnownCellIdentity",
                value = null
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                setMethodResult(
                    methodName = "getLocationData",
                    value = INCLUDE_LOCATION_DATA_NONE
                )
            }
        }

        PhoneStateListener::class.java
            .setSomeSameNameMethodResult(
                "onCellLocationChanged",
                "onCellInfoChanged",
                value = null
            )

    }

    /**
     * Disables Wi-Fi-based location by making [WifiManager] report Wi-Fi as
     * disabled and returning empty scan results. Also zeroes out the device's
     * Wi-Fi MAC address and BSSID.
     *
     * Hooked methods:
     * - [WifiManager.getScanResults] -- returns an empty list.
     * - [WifiManager.isWifiEnabled] -- returns `false`.
     * - [WifiManager.isScanAlwaysAvailable] -- returns `false`.
     * - [WifiManager.getWifiState] -- returns [WifiManager.WIFI_STATE_DISABLED].
     * - [WifiInfo.getMacAddress] / [WifiInfo.getBSSID] -- returns `"00:00:00:00:00:00"`.
     */
    protected fun makeWifiLocationFail() {
        WifiManager::class.java.run {
            setMethodResult("getScanResults", emptyList<ScanResult>())
            setMethodResult("isWifiEnabled", false)
            setMethodResult("isScanAlwaysAvailable", false)
            setMethodResult("getWifiState", WifiManager.WIFI_STATE_DISABLED)
        }
        WifiInfo::class.java.run {
            setMethodResult("getMacAddress", "00:00:00:00:00:00")
            setMethodResult("getBSSID", "00:00:00:00:00:00")
        }
    }

    /**
     * Invalidates [GsmCellLocation] identifiers by returning `-1` for
     * both PSC (Primary Scrambling Code) and LAC (Location Area Code),
     * causing cell-tower-based location lookups to fail.
     */
    protected fun makeCellLocationFail() {
        GsmCellLocation::class.java.run {
            setMethodResult("getPsc", -1)
            setMethodResult("getLac", -1)
        }
    }


}
