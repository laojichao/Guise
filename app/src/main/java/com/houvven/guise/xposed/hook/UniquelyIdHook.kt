package com.houvven.guise.xposed.hook

import android.content.ContentResolver
import android.provider.Settings
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.guise.xposed.PackageConfig
import com.houvven.guise.xposed.config.ModuleConfig
import com.houvven.ktx_xposed.hook.afterHookedMethod
import com.houvven.ktx_xposed.hook.beforeHookedMethod
import com.houvven.ktx_xposed.hook.findClass
import com.houvven.ktx_xposed.hook.findClassIfExists
import com.houvven.ktx_xposed.hook.lppram
import com.houvven.ktx_xposed.hook.setAllMethodResult
import com.houvven.ktx_xposed.hook.setMethodResult
import com.houvven.ktx_xposed.hook.setSomeSameNameMethodResult
import com.houvven.ktx_xposed.logger.XposedLogger

/**
 * Xposed hook that spoofs device-unique identifiers reported to the target application.
 *
 * Supports overriding the following identifiers:
 * - **Android ID** ([Settings.Secure.ANDROID_ID]) -- spoofed via hooks on both
 *   [Secure.getStringForUser] and [Settings.System.getStringForUser].
 * - **IMEI** ([TelephonyManager.getImei]) -- spoofed by replacing the method result.
 * - **Phone number** ([TelephonyManager.getLine1Number]) -- spoofed across all overloads.
 *
 * Android ID spoofing has a fallback mechanism for multi-process apps (e.g., WebView
 * renderers): when the primary config's `androidId` is blank, the hook attempts to read
 * the configuration from shared preferences keyed by the current process name.
 */
class UniquelyIdHook : LoadPackageHandler {

    /**
     * Installs hooks for each configured identifier override.
     *
     * Each sub-hook is only installed when its corresponding configuration value is
     * non-blank (non-empty string), ensuring that unset identifiers are not touched.
     */
    override fun onHook() {
        if (config.androidId.isNotBlank()) this.hookAndroidId()
        if (config.imei.isNotBlank()) this.hookImei()
        if (config.phoneNum.isNotBlank()) this.hookPhoneNum()
    }

    /**
     * Hooks [Secure.getStringForUser] and [Settings.System.getStringForUser] to return
     * a spoofed Android ID.
     *
     * Uses [beforeHookedMethod] on [Secure] and [afterHookedMethod] on [Settings.System]
     * to cover both API paths. When [config].androidId is blank (e.g., in a child process
     * where the config was not fully populated), falls back to reading the identifier from
     * [PackageConfig.xSharedPrefs] keyed by [lppram].processName.
     */
    private fun hookAndroidId() {
        Secure::class.java.beforeHookedMethod(
            methodName = "getStringForUser",
            ContentResolver::class.java, String::class.java, Int::class.java
        ) { param ->
            if (param.args[1] == Secure.ANDROID_ID) {
                if (config.androidId.isBlank()) {
                    // Fallback for child processes (e.g., WebView renderers):
                    // read the config from shared preferences by process name.
                    XposedLogger.i("androidId is blank")
                    XposedLogger.i("Web view processName: ${lppram.processName}")
                    PackageConfig.xSharedPrefs.getString(lppram.processName, "")!!.let { json ->
                        if (json.isNotBlank()) {
                            val moduleConfig = ModuleConfig.fromJson(json)
                            param.result = moduleConfig.androidId
                        }
                    }
                } else {
                    param.result = config.androidId
                }
            }
        }


        Settings.System::class.java.afterHookedMethod(
            methodName = "getStringForUser",
            ContentResolver::class.java, String::class.java, Int::class.java
        ) { param ->
            if (param.args[1] == Settings.System.ANDROID_ID) {
                if (config.androidId.isBlank()) {
                    // Same fallback logic as above for the System settings path.
                    XposedLogger.i("androidId is blank")
                    XposedLogger.i("Web view processName: ${lppram.processName}")
                    PackageConfig.xSharedPrefs.getString(lppram.processName, "")!!.let { json ->
                        if (json.isNotBlank()) {
                            val moduleConfig = ModuleConfig.fromJson(json)
                            param.result = moduleConfig.androidId
                        }
                    }
                } else {
                    param.result = config.androidId
                }
            }
        }

    }

    /**
     * Hooks [TelephonyManager.getImei] (the single-argument overload that accepts a slot index)
     * to return the configured IMEI value.
     */
    private fun hookImei() {
        TelephonyManager::class.java.setMethodResult(
            "getImei", config.imei, parameterTypes = arrayOf(Int::class.java)
        )
    }

    /**
     * Hooks all overloads of [TelephonyManager.getLine1Number] to return the configured
     * phone number.
     */
    private fun hookPhoneNum() {
        TelephonyManager::class.java.setAllMethodResult("getLine1Number", config.phoneNum)
    }

}