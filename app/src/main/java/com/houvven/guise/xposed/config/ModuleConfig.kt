package com.houvven.guise.xposed.config

import com.houvven.guise.xposed.PackageConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

/**
 * Serializable data class representing the full set of Xposed hook configuration
 * for a single target application package.
 *
 * Each field corresponds to a spoofable property or behavior. Empty string fields
 * and sentinel numeric values (e.g., -1) indicate "no override" -- the hook handler
 * should leave the original value unchanged.
 *
 * Instances are serialized to/from JSON via [kotlinx.serialization] and stored in
 * SharedPreferences keyed by package name. The [isEnable] property determines whether
 * any hooks should be applied at all.
 *
 * @property packageName the target application's package name (transient, not serialized)
 * @property brand spoofed device manufacturer (e.g., "Samsung")
 * @property model spoofed device model (e.g., "SM-G9910")
 * @property product spoofed build product name
 * @property device spoofed build device name
 * @property board spoofed hardware board name
 * @property hardware spoofed hardware platform name
 * @property androidVersion spoofed Android version string (e.g., "13")
 * @property sdkInt spoofed SDK API level; -1 means no override
 * @property networkType spoofed network type; one of [HooksValue.NET_*] constants
 * @property fingerPrint spoofed Android build fingerprint
 * @property wifiSSID spoofed Wi-Fi network SSID
 * @property wifiBSSID spoofed Wi-Fi access point BSSID
 * @property wifiMacAddress spoofed Wi-Fi MAC address
 * @property simOperator spoofed SIM operator numeric code (MCC+MNC)
 * @property simOperatorName spoofed SIM operator display name
 * @property simCountry spoofed SIM country ISO code
 * @property imei spoofed device IMEI
 * @property phoneNum spoofed phone number
 * @property androidId spoofed Android Settings.Secure ID
 * @property lac spoofed Location Area Code; -1 means no override
 * @property cid spoofed Cell ID; -1 means no override
 * @property language spoofed system locale/language
 * @property longitude spoofed GPS longitude; -1.0 means no override
 * @property latitude spoofed GPS latitude; -1.0 means no override
 * @property randomOffset whether to add a random offset to spoofed coordinates
 * @property makeWifiLocationFail whether to force Wi-Fi-based location requests to fail
 * @property makeCellLocationFail whether to force cell-based location requests to fail
 * @property versionCode spoofed target app version code; -1 means no override
 * @property versionName spoofed target app version name
 * @property batteryLevel spoofed battery level (0-100); -1 means no override
 * @property screenshotsFlag screenshot policy; one of [HooksValue.SCREENSHOTS_*] constants
 * @property hookSuccessHint whether to show a toast when hooks are successfully applied
 * @property passContacts whether to bypass contacts permission checks
 * @property passPhoto whether to bypass photo/gallery permission checks
 * @property passVideo whether to bypass video permission checks
 * @property passAudio whether to bypass audio/microphone permission checks
 */
@Serializable
data class ModuleConfig(
    @Transient var packageName: String = "",
    var brand: String = "",
    var model: String = "",
    var product: String = "",
    var device: String = "",
    var board: String = "",
    var hardware: String = "",
    var androidVersion: String = "",
    var sdkInt: Int = -1,
    var networkType: Int = HooksValue.NET_UNHOOK,
    var fingerPrint: String = "",
    var wifiSSID: String = "",
    var wifiBSSID: String = "",
    var wifiMacAddress: String = "",
    var simOperator: String = "",
    var simOperatorName: String = "",
    var simCountry: String = "",
    var imei: String = "",
    var phoneNum: String = "",
    var androidId: String = "",
    var lac: Int = -1,
    var cid: Int = -1,
    var language: String = "",
    var longitude: Double = -1.0,
    var latitude: Double = -1.0,
    var randomOffset: Boolean = false,
    var makeWifiLocationFail: Boolean = false,
    var makeCellLocationFail: Boolean = false,

    var versionCode: Int = -1,
    var versionName: String = "",
    var batteryLevel: Int = -1,
    var screenshotsFlag: Int = HooksValue.SCREENSHOTS_UNHOOK,
    var hookSuccessHint: Boolean = false,
    var passContacts: Boolean = false,
    var passPhoto: Boolean = false,
    var passVideo: Boolean = false,
    var passAudio: Boolean = false,
) {
    /**
     * Whether this configuration has any non-default (non-empty) values,
     * indicating that hooks should be applied for the target package.
     *
     * Returns `true` if this instance differs from a freshly constructed
     * [ModuleConfig] with the same [packageName].
     */
    val isEnable: Boolean get() = this != ModuleConfig(packageName)

    /**
     * Serializes this configuration to a JSON string.
     *
     * @return the JSON-encoded representation of this [ModuleConfig]
     */
    fun toJson() = Json.encodeToString(serializer(), this)

    /**
     * Converts this configuration into a Compose-observable [ModuleConfigState].
     *
     * @return a new [ModuleConfigState] initialized from this configuration's values
     */
    fun toModuleConfigState() = ModuleConfigState.of(this)

    companion object {
        /**
         * Deserializes a [ModuleConfig] from its JSON string representation.
         *
         * @param json the JSON string previously produced by [toJson]
         * @return the deserialized [ModuleConfig] instance
         * @throws kotlinx.serialization.SerializationException if the JSON is malformed
         */
        fun fromJson(json: String) = Json.decodeFromString(serializer(), this)

        /**
         * Retrieves the saved [ModuleConfig] for the given [packageName] from
         * SharedPreferences, or returns a default (disabled) instance if none exists.
         *
         * @param packageName the Android package name to look up
         * @return the persisted [ModuleConfig], or a new default instance
         */
        fun get(packageName: String): ModuleConfig {
            val config = PackageConfig.safePrefs.getString(packageName, null)?.let { fromJson(it) }
            config?.packageName = packageName
            return config ?: ModuleConfig(packageName)
        }
    }
}
