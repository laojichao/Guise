package com.houvven.guise.module.preset

import com.houvven.guise.module.PresetAdapter

/**
 * Enumerates predefined Android OS version presets for OS version spoofing.
 *
 * Each constant maps a human-readable Android version name ([label]) to its
 * corresponding API level string ([value]). These presets allow the Xposed hook
 * framework to override the Android OS version reported to target applications,
 * which is useful for bypassing minimum-version checks or testing backward
 * compatibility behavior.
 *
 * The set covers Android versions from 1.0 (API level 1) through Android 13
 * (API level 33).
 *
 * Implements [PresetAdapter] so instances can be directly rendered in UI selection
 * components (e.g., spinners, dropdown menus).
 *
 * @see PresetAdapter
 */
enum class ReleasePreset(override val label: String, override val value: String) : PresetAdapter {

    /** @property ANDROID_1 Android 1.0, API level 1. */
    ANDROID_1("Android 1", "1"),

    /** @property ANDROID_1_1 Android 1.1, API level 2. */
    ANDROID_1_1("Android 1.1", "1.1"),

    /** @property ANDROID_1_5 Android 1.5 Cupcake, API level 3. */
    ANDROID_1_5("Android 1.5", "1.5"),

    /** @property ANDROID_1_6 Android 1.6 Donut, API level 4. */
    ANDROID_1_6("Android 1.6", "1.6"),

    /** @property ANDROID_2_0 Android 2.0 Eclair, API level 5. */
    ANDROID_2_0("Android 2.0", "2"),

    /** @property ANDROID_2_0_1 Android 2.0.1 Eclair, API level 6. */
    ANDROID_2_0_1("Android 2.0.1", "2.0.1"),

    /** @property ANDROID_2_1 Android 2.1 Eclair, API level 7. */
    ANDROID_2_1("Android 2.1", "2.1"),

    /** @property ANDROID_2_2 Android 2.2 Froyo, API level 8. */
    ANDROID_2_2("Android 2.2", "2.2"),

    /** @property ANDROID_2_3 Android 2.3 Gingerbread, API level 9. */
    ANDROID_2_3("Android 2.3", "2.3"),

    /** @property ANDROID_2_3_3 Android 2.3.3 Gingerbread, API level 10. */
    ANDROID_2_3_3("Android 2.3.3", "2.3.3"),

    /** @property ANDROID_3_0 Android 3.0 Honeycomb, API level 11. */
    ANDROID_3_0("Android 3.0", "3"),

    /** @property ANDROID_3_1 Android 3.1 Honeycomb, API level 12. */
    ANDROID_3_1("Android 3.1", "3.1"),

    /** @property ANDROID_3_2 Android 3.2 Honeycomb, API level 13. */
    ANDROID_3_2("Android 3.2", "3.2"),

    /** @property ANDROID_4_0 Android 4.0 Ice Cream Sandwich, API level 14. */
    ANDROID_4_0("Android 4.0", "4"),

    /** @property ANDROID_4_0_3 Android 4.0.3 Ice Cream Sandwich, API level 15. */
    ANDROID_4_0_3("Android 4.0.3", "4.0.3"),

    /** @property ANDROID_4_1 Android 4.1 Jelly Bean, API level 16. */
    ANDROID_4_1("Android 4.1", "4.1"),

    /** @property ANDROID_4_2 Android 4.2 Jelly Bean, API level 17. */
    ANDROID_4_2("Android 4.2", "4.2"),

    /** @property ANDROID_4_3 Android 4.3 Jelly Bean, API level 18. */
    ANDROID_4_3("Android 4.3", "4.3"),

    /** @property ANDROID_4_4 Android 4.4 KitKat, API level 19. */
    ANDROID_4_4("Android 4.4", "4.4"),

    /** @property ANDROID_4_4W Android 4.4W KitKat Wear, API level 20. */
    ANDROID_4_4W("Android 4.4W", "4.4W"),

    /** @property ANDROID_5_0 Android 5.0 Lollipop, API level 21. */
    ANDROID_5_0("Android 5.0", "5"),

    /** @property ANDROID_5_1 Android 5.1 Lollipop, API level 22. */
    ANDROID_5_1("Android 5.1", "5.1"),

    /** @property ANDROID_6_0 Android 6.0 Marshmallow, API level 23. */
    ANDROID_6_0("Android 6.0", "6"),

    /** @property ANDROID_7_0 Android 7.0 Nougat, API level 24. */
    ANDROID_7_0("Android 7.0", "7"),

    /** @property ANDROID_7_1 Android 7.1 Nougat, API level 25. */
    ANDROID_7_1("Android 7.1", "7.1"),

    /** @property ANDROID_8_0 Android 8.0 Oreo, API level 26. */
    ANDROID_8_0("Android 8.0", "8"),

    /** @property ANDROID_8_1 Android 8.1 Oreo, API level 27. */
    ANDROID_8_1("Android 8.1", "8.1"),

    /** @property ANDROID_9_0 Android 9.0 Pie, API level 28. */
    ANDROID_9_0("Android 9.0", "9"),

    /** @property ANDROID_10_0 Android 10, API level 29. */
    ANDROID_10_0("Android 10", "10"),

    /** @property ANDROID_11_0 Android 11, API level 30. */
    ANDROID_11_0("Android 11", "11"),

    /** @property ANDROID_12_0 Android 12, API level 31. */
    ANDROID_12_0("Android 12", "12"),

    /** @property ANDROID_13_0 Android 13, API level 33. */
    ANDROID_13_0("Android 13", "13")

    ;
}
