package com.houvven.guise.xposed.hook.netowork

import android.telephony.CellIdentityCdma
import android.telephony.CellIdentityGsm
import android.telephony.CellIdentityLte
import android.telephony.CellIdentityNr
import android.telephony.CellIdentityTdscdma
import android.telephony.CellIdentityWcdma
import android.telephony.SubscriptionInfo
import android.telephony.TelephonyManager
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.guise.xposed.config.HooksValue
import com.houvven.ktx_xposed.hook.findMethodExactIfExists
import com.houvven.ktx_xposed.hook.setMethodResult
import com.houvven.ktx_xposed.hook.setSomeSameNameMethodResult
import com.houvven.ktx_xposed.hook.setSomeSameNameMethodResultForAnyClass

/**
 * Xposed hook that spoofs SIM card and telephony identity information
 * for the target application.
 *
 * This hook intercepts APIs across [TelephonyManager] and [SubscriptionInfo]
 * to replace the device's real carrier and SIM identity with user-configured values.
 * It covers three independent spoofing dimensions:
 *
 * 1. **SIM Operator** ([config.simOperator]) -- Overrides the numeric operator code
 *    (MCC+MNC) returned by various telephony and cell identity methods.
 *    The operator string is split into MCC (first 3 digits) and MNC (remaining digits)
 *    and applied to both integer and string return variants across all
 *    [android.telephony.CellIdentity] subclasses.
 *
 * 2. **Operator Name** ([config.simOperatorName]) -- Overrides the human-readable
 *    carrier/operator name returned by [TelephonyManager] and [SubscriptionInfo].
 *
 * 3. **Country ISO** ([config.simCountry]) -- Overrides the SIM/network country
 *    ISO code returned by [TelephonyManager] and [SubscriptionInfo].
 *
 * Additionally, provides [hookMobileType] for spoofing the detailed mobile
 * network generation (2G through 5G) on [TelephonyManager.getNetworkType].
 */
internal class SimHook : LoadPackageHandler {
    override fun onHook() {
        if (config.simOperator.isNotBlank()) this.hookSimOperator()
        if (config.simOperatorName.isNotBlank()) this.hookSimOperatorName()
        if (config.simCountry.isNotBlank()) this.hookSimCountryIso()
    }

    /**
     * Overrides [TelephonyManager.getNetworkType] to return the telephony
     * network type constant corresponding to the configured mobile generation.
     *
     * Mapping:
     * - [HooksValue.NET_MOBILE_2G] -> [TelephonyManager.NETWORK_TYPE_CDMA]
     * - [HooksValue.NET_MOBILE_3G] -> [TelephonyManager.NETWORK_TYPE_TD_SCDMA]
     * - [HooksValue.NET_MOBILE_4G] -> [TelephonyManager.NETWORK_TYPE_LTE]
     * - [HooksValue.NET_MOBILE_5G] -> [TelephonyManager.NETWORK_TYPE_NR]
     * - Any other value -> [TelephonyManager.NETWORK_TYPE_UNKNOWN]
     *
     * @param networkType the configured network type constant from [HooksValue].
     */
    internal fun hookMobileType(networkType: Int) {
        val type = when (networkType) {
            HooksValue.NET_MOBILE_2G -> TelephonyManager.NETWORK_TYPE_CDMA
            HooksValue.NET_MOBILE_3G -> TelephonyManager.NETWORK_TYPE_TD_SCDMA
            HooksValue.NET_MOBILE_4G -> TelephonyManager.NETWORK_TYPE_LTE
            HooksValue.NET_MOBILE_5G -> TelephonyManager.NETWORK_TYPE_NR
            else -> TelephonyManager.NETWORK_TYPE_UNKNOWN
            // else -> networkType
        }
        TelephonyManager::class.java.setMethodResult("getNetworkType", type)
    }

    /**
     * Overrides SIM operator numeric codes (MCC+MNC) across [TelephonyManager]
     * and all [android.telephony.CellIdentity] subclasses.
     *
     * The [config.simOperator] string (e.g., "46000") is parsed into:
     * - MCC: first 3 characters ("460")
     * - MNC: remaining characters ("00")
     *
     * Both integer and string variants of MCC/MNC methods are hooked on:
     * [SubscriptionInfo], [CellIdentityCdma], [CellIdentityGsm],
     * [CellIdentityLte], [CellIdentityNr], [CellIdentityTdscdma],
     * and [CellIdentityWcdma].
     */
    private fun hookSimOperator() {
        val simOperator = config.simOperator
        val mcc = simOperator.substring(0, 3)
        val mnc = simOperator.substring(3)
        val mccInt = mcc.toIntOrNull()
        val mncInt = mnc.toIntOrNull()

        TelephonyManager::class.java.run {
            setSomeSameNameMethodResult(
                "getSimOperatorNumericForPhone",
                "getNetworkOperatorForPhone",
                "getSimOperator",
                "getNetworkOperator",
                value = simOperator
            )
        }

        arrayOf(
            SubscriptionInfo::class.java,
            CellIdentityCdma::class.java,
            CellIdentityGsm::class.java,
            CellIdentityLte::class.java,
            CellIdentityNr::class.java,
            CellIdentityTdscdma::class.java,
            CellIdentityWcdma::class.java
        ).forEach {
            it.run {
                findMethodExactIfExists("getMcc")?.setMethodResult(mccInt)
                findMethodExactIfExists("getMnc")?.setMethodResult(mncInt)
                findMethodExactIfExists("getMccString")?.setMethodResult(mcc)
                findMethodExactIfExists("getMncString")?.setMethodResult(mnc)
            }
        }
    }

    /**
     * Overrides the human-readable SIM operator / carrier name across
     * [TelephonyManager] and [SubscriptionInfo].
     *
     * Hooked methods:
     * - [TelephonyManager.getSimOperatorName]
     * - [TelephonyManager.getSimOperatorNameForPhone]
     * - [TelephonyManager.getNetworkOperatorName]
     * - [TelephonyManager.getNetworkOperatorNameForPhone]
     * - [SubscriptionInfo.getCarrierName]
     * - [SubscriptionInfo.getDisplayName]
     *
     * @return always returns [String] (unreachable; required by Kotlin expression body).
     */
    private fun hookSimOperatorName() {
        setSomeSameNameMethodResultForAnyClass(
            listOf(
                TelephonyManager::class.java to "getSimOperatorName",
                TelephonyManager::class.java to "getSimOperatorNameForPhone",
                TelephonyManager::class.java to "getNetworkOperatorName",
                TelephonyManager::class.java to "getNetworkOperatorNameForPhone",
                SubscriptionInfo::class.java to "getCarrierName",
                SubscriptionInfo::class.java to "getDisplayName",
            ),
            value = config.simOperatorName
        )
        String
    }

    /**
     * Overrides the SIM / network country ISO code across [TelephonyManager]
     * and [SubscriptionInfo].
     *
     * Hooked methods:
     * - [TelephonyManager.getSimCountryIso]
     * - [TelephonyManager.getSimCountryIsoForPhone]
     * - [TelephonyManager.getNetworkCountryIso]
     * - [TelephonyManager.getNetworkCountryIsoForPhone]
     * - [SubscriptionInfo.getCountryIso]
     */
    private fun hookSimCountryIso() {
        setSomeSameNameMethodResultForAnyClass(
            listOf(
                TelephonyManager::class.java to "getSimCountryIso",
                TelephonyManager::class.java to "getSimCountryIsoForPhone",
                TelephonyManager::class.java to "getNetworkCountryIso",
                TelephonyManager::class.java to "getNetworkCountryIsoForPhone",
                SubscriptionInfo::class.java to "getCountryIso"
            ),
            value = config.simCountry
        )
    }
}
