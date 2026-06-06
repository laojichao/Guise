package com.houvven.guise.xposed.hook

import android.os.Build
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setStaticField

/**
 * Xposed hook that spoofs [android.os.Build] and [android.os.Build.VERSION] static fields
 * to disguise the device identity for the target application.
 *
 * Supports overriding the following groups of properties:
 * - **Device identity**: [Build.BRAND], [Build.MANUFACTURER], [Build.MODEL], [Build.PRODUCT],
 *   [Build.DEVICE], [Build.BOARD], [Build.HARDWARE], [Build.FINGERPRINT]
 * - **OS version**: [Build.VERSION.SDK_INT], [Build.VERSION.RELEASE], [Build.VERSION.BASE_OS]
 *
 * Each field is only overwritten when the corresponding configuration value is non-blank
 * (for string fields) or not equal to -1 (for numeric fields such as SDK_INT).
 */
class OsBuildHook : LoadPackageHandler {

    /**
     * Reads spoofed device properties from [config] and overwrites the corresponding
     * static fields on [Build] and [Build.VERSION].
     *
     * String fields (brand, model, etc.) are skipped when their config value is blank.
     * Numeric fields (sdkInt) and derived strings (androidVersion, baseOs) are skipped
     * when their value is blank or equals the sentinel "-1".
     */
    override fun onHook() {
        config.run {
            // Overwrite string-based device identity fields on Build.
            mapOf(
                arrayOf("BRAND", "MANUFACTURER") to brand,
                arrayOf("MODEL") to model,
                arrayOf("PRODUCT") to product,
                arrayOf("DEVICE") to device,
                arrayOf("BOARD") to board,
                arrayOf("HARDWARE") to hardware,
                arrayOf("FINGERPRINT") to fingerPrint,
            ).forEach { (fields, value) ->
                if (value.isNotBlank()) fields.forEach { field ->
                    Build::class.java.setStaticField(field, value)
                }
            }

            // Overwrite version-related fields on Build.VERSION.
            mapOf(
                arrayOf("SDK_INT") to sdkInt,
                arrayOf("RELEASE") to androidVersion,
                arrayOf("BASE_OS") to fingerPrint,
            ).forEach { (fields, value) ->
                if (value.toString().isNotBlank() && value.toString() != "-1") {
                    fields.forEach { field ->
                        Build.VERSION::class.java.setStaticField(field, value)
                    }
                }
            }
        }


    }

}