package com.houvven.guise.xposed.config

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * Compose-observable wrapper around [ModuleConfig] that exposes each configuration
 * field as a [MutableState] for two-way UI binding.
 *
 * This class mirrors every relevant field from [ModuleConfig] but represents them as
 * [MutableState] holders, enabling Jetpack Compose UI components to read and write
 * configuration values reactively. The [init] block uses reflection to automatically
 * populate each state field from the source [ModuleConfig].
 *
 * Numeric and enum values (Int, Long, Double, etc.) are stored as [MutableState]<String>
 * so that text field inputs can directly bind to them. Boolean fields retain their
 * native [MutableState]<Boolean> type.
 *
 * Use [of] in the [companion object][Companion] to create instances.
 */
class ModuleConfigState private constructor(moduleConfig: ModuleConfig) {

    // ── Device identity properties ──────────────────────────────────────────

    /** Spoofed device manufacturer/brand (e.g., "Samsung", "Google"). */
    lateinit var brand: MutableState<String>

    /** Spoofed device model name (e.g., "Pixel 7"). */
    lateinit var model: MutableState<String>

    /** Spoofed build product identifier. */
    lateinit var product: MutableState<String>

    /** Spoofed build device identifier. */
    lateinit var device: MutableState<String>

    /** Spoofed hardware board name. */
    lateinit var board: MutableState<String>

    /** Spoofed hardware platform name. */
    lateinit var hardware: MutableState<String>

    /** Spoofed Android version string (e.g., "13"). */
    lateinit var androidVersion: MutableState<String>

    /** Spoofed SDK API level as a string; empty means no override. */
    lateinit var sdkInt: MutableState<String>

    /** Spoofed Android build fingerprint. */
    lateinit var fingerPrint: MutableState<String>

    // ── Network properties ──────────────────────────────────────────────────

    /** Spoofed network type as a string value; maps to [HooksValue.NET_*] constants. */
    lateinit var networkType: MutableState<String>

    /** Spoofed Wi-Fi SSID. */
    lateinit var wifiSSID: MutableState<String>

    /** Spoofed Wi-Fi access point BSSID (MAC address). */
    lateinit var wifiBSSID: MutableState<String>

    /** Spoofed Wi-Fi interface MAC address. */
    lateinit var wifiMacAddress: MutableState<String>

    /** Spoofed SIM operator numeric code (MCC+MNC). */
    lateinit var simOperator: MutableState<String>

    /** Spoofed SIM operator display name (e.g., "China Mobile"). */
    lateinit var simOperatorName: MutableState<String>

    /** Spoofed SIM country ISO code (e.g., "CN"). */
    lateinit var simCountry: MutableState<String>

    // ── Device identifiers ──────────────────────────────────────────────────

    /** Spoofed device IMEI. */
    lateinit var imei: MutableState<String>

    /** Spoofed phone number. */
    lateinit var phoneNum: MutableState<String>

    /** Spoofed Android Settings.Secure.ANDROID_ID. */
    lateinit var androidId: MutableState<String>

    // ── Cell location properties ────────────────────────────────────────────

    /** Spoofed Location Area Code (LAC) as a string; empty means no override. */
    lateinit var lac: MutableState<String>

    /** Spoofed Cell ID (CID) as a string; empty means no override. */
    lateinit var cid: MutableState<String>

    // ── GPS location properties ─────────────────────────────────────────────

    /** Spoofed GPS longitude as a string; empty means no override. */
    lateinit var longitude: MutableState<String>

    /** Spoofed GPS latitude as a string; empty means no override. */
    lateinit var latitude: MutableState<String>

    /** Whether to add a random offset to the spoofed GPS coordinates. */
    lateinit var randomOffset: MutableState<Boolean>

    /** Whether to force Wi-Fi-based location requests to fail. */
    lateinit var makeWifiLocationFail: MutableState<Boolean>

    /** Whether to force cell-based location requests to fail. */
    lateinit var makeCellLocationFail: MutableState<Boolean>

    // ── Application and misc properties ─────────────────────────────────────

    /** Spoofed target app version code as a string; empty means no override. */
    lateinit var versionCode: MutableState<String>

    /** Spoofed target app version name. */
    lateinit var versionName: MutableState<String>

    /** Spoofed battery level as a string; empty means no override. */
    lateinit var batteryLevel: MutableState<String>

    /** Spoofed system locale/language. */
    lateinit var language: MutableState<String>

    /** Screenshot policy flag as a string; maps to [HooksValue.SCREENSHOTS_*] constants. */
    lateinit var screenshotsFlag: MutableState<String>

    /** Whether to show a toast notification when hooks are successfully applied. */
    lateinit var hookSuccessHint: MutableState<Boolean>

    /** Whether to bypass contacts permission checks. */
    lateinit var passContacts: MutableState<Boolean>

    /** Whether to bypass photo/gallery permission checks. */
    lateinit var passPhoto: MutableState<Boolean>

    /** Whether to bypass video permission checks. */
    lateinit var passVideo: MutableState<Boolean>

    /** Whether to bypass audio/microphone permission checks. */
    lateinit var passAudio: MutableState<Boolean>

    /**
     * Initializes all [MutableState] fields by reflecting over [ModuleConfig]'s fields.
     *
     * For each field in [ModuleConfig] that has a matching [MutableState] field in this class:
     * - Boolean values are wrapped directly as [MutableState]<Boolean>
     * - String values are wrapped as [MutableState]<String>
     * - Numeric values (Int, Long, etc.) are converted to their string representation;
     *   default/sentinel values are replaced with an empty string so that text fields
     *   appear blank rather than showing "-1"
     */
    init {
        val stateFields = this.javaClass.declaredFields
            .filter { it.type == MutableState::class.java }
            .toMutableList()
        val configFields = moduleConfig.javaClass.declaredFields
        val empty = ModuleConfig()
        for (configField in configFields) {
            val stateField = stateFields.find { it.name == configField.name }
            if (stateField != null) {
                configField.isAccessible = true
                when (val value = configField.get(moduleConfig)) {
                    is Boolean -> stateField.set(this, mutableStateOf(value))
                    is String -> stateField.set(this, mutableStateOf(value.toString()))
                    else -> {
                        // Use empty string for default/sentinel values to show blank in UI
                        val v = if (configField.get(empty) == value) "" else value.toString()
                        stateField.set(this, mutableStateOf(v))
                    }
                }
                stateFields.remove(stateField)
            }
            // Early exit once all state fields have been matched
            if (stateFields.isEmpty()) break
        }
    }

    /**
     * Resets all state fields to their default values.
     *
     * Boolean fields are set to `false`, and String fields are set to empty string.
     */
    internal fun clear() {
        val stateFields =
            this.javaClass.declaredFields.filter { it.type == MutableState::class.java }
        for (stateField in stateFields) {
            val state = (stateField.get(this) as MutableState<*>)
            if (state.value is Boolean) (state as MutableState<Boolean>).value = false
            else (state as MutableState<String>).value = ""
        }
    }

    companion object {
        /**
         * Creates a new [ModuleConfigState] initialized from the given [ModuleConfig].
         *
         * @param moduleConfig the source configuration whose values populate the state fields
         * @return a new [ModuleConfigState] instance with all fields initialized
         */
        fun of(moduleConfig: ModuleConfig) = ModuleConfigState(moduleConfig)
    }

}
