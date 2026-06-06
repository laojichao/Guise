package com.houvven.guise.module.preset

import com.houvven.guise.module.PresetAdapter
import com.houvven.guise.xposed.config.HooksValue

/**
 * Enumerates predefined network type configurations for connectivity spoofing.
 *
 * Each constant maps a human-readable network label ([label]) to an integer value
 * ([value]) that corresponds to a [HooksValue] network constant. These presets are
 * consumed by the Xposed hook layer to override the network type reported by the
 * Android framework, enabling users to simulate different connectivity states
 * (e.g., forcing Wi-Fi mode on a cellular connection, or reporting no network).
 *
 * The underlying integer values are derived from [HooksValue] constants to ensure
 * consistency with the hook configuration system.
 *
 * Implements [PresetAdapter] so instances can be directly rendered in UI selection
 * components (e.g., spinners, dropdown menus).
 *
 * @see PresetAdapter
 * @see HooksValue
 */
enum class NetworkPreset(override val label: String, override val value: String) :
    PresetAdapter {

    /** @property MOBILE_5G Reports network type as 5G mobile data. Maps to [HooksValue.NET_MOBILE_5G]. */
    MOBILE_5G("5G", HooksValue.NET_MOBILE_5G.toString()),

    /** @property MOBILE_4G Reports network type as 4G (LTE) mobile data. Maps to [HooksValue.NET_MOBILE_4G]. */
    MOBILE_4G("4G", HooksValue.NET_MOBILE_4G.toString()),

    /** @property MOBILE_3G Reports network type as 3G mobile data. Maps to [HooksValue.NET_MOBILE_3G]. */
    MOBILE_3G("3G", HooksValue.NET_MOBILE_3G.toString()),

    /** @property MOBILE_2G Reports network type as 2G mobile data. Maps to [HooksValue.NET_MOBILE_2G]. */
    MOBILE_2G("2G", HooksValue.NET_MOBILE_2G.toString()),

    /** @property WIFI Reports network type as Wi-Fi. Maps to [HooksValue.NET_WIFI]. */
    WIFI("WiFi", HooksValue.NET_WIFI.toString()),

    /** @property NONE Reports no active network connection. Maps to [HooksValue.NET_NONE]. */
    NONE("None", HooksValue.NET_NONE.toString());

}
