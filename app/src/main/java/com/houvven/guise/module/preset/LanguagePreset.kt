package com.houvven.guise.module.preset

import com.houvven.guise.module.PresetAdapter

/**
 * Enumerates predefined locale configurations for device language spoofing.
 *
 * Each constant maps a human-readable language name ([label]) to a locale string
 * ([value]) in the `language_REGION` format (e.g., `"en_US"`, `"zh_CN"`). These
 * presets are used by the Xposed hook framework to override the target application's
 * perceived device locale, allowing users to test or bypass region-locked content
 * without changing the system language.
 *
 * Implements [PresetAdapter] so instances can be directly rendered in UI selection
 * components (e.g., spinners, dropdown menus).
 *
 * @see PresetAdapter
 */
enum class LanguagePreset(override val label: String, override val value: String) : PresetAdapter {

    /** @property SimplifiedChinese Simplified Chinese (Mainland China), locale `zh_CN`. */
    SimplifiedChinese("简体中文", "zh_CN"),

    /** @property HansChinese Simplified Chinese with Hans script tag, locale `zh_Hans`. */
    HansChinese("简体中文(Hans)", "zh_Hans"),

    /** @property TraditionalChineseTW Traditional Chinese (Taiwan), locale `zh_TW`. */
    TraditionalChineseTW("繁體中文(TW)", "zh_TW"),

    /** @property TraditionalChineseHK Traditional Chinese (Hong Kong), locale `zh_HK`. */
    TraditionalChineseHK("繁體中文(HK)", "zh_HK"),

    /** @property English English (United States), locale `en_US`. */
    English("English", "en_US"),

    /** @property Japanese Japanese (Japan), locale `ja_JP`. */
    Japanese("日本語", "ja_JP"),

    /** @property Korean Korean (South Korea), locale `ko_KR`. */
    Korean("한국어", "ko_KR"),

    /** @property French French (France), locale `fr_FR`. */
    French("Français", "fr_FR"),

    /** @property German German (Germany), locale `de_DE`. */
    German("Deutsch", "de_DE"),

    /** @property Malay Malay (Malaysia), locale `ms_MY`. */
    Malay("Bahasa Melayu", "ms_MY"),

    /** @property Indonesian Indonesian (Indonesia), locale `id_ID`. */
    Indonesian("Bahasa Indonesia", "id_ID"),

    /** @property Thai Thai (Thailand), locale `th_TH`. */
    Thai("ภาษาไทย", "th_TH"),

    /** @property Vietnamese Vietnamese (Vietnam), locale `vi_VN`. */
    Vietnamese("Tiếng Việt", "vi_VN"),
    ;
}
