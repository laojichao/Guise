package com.houvven.guise.xposed.hook

import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setMethodResult
import java.util.Locale

/**
 * Xposed hook that spoofs the system locale reported to the target application.
 *
 * Parses the configured language string (e.g., `"en"`, `"zh_CN"`) into a [Locale],
 * then hooks all relevant static and instance query methods on [Locale] so that any
 * locale lookup returns consistent spoofed values. This includes:
 * - [Locale.getDefault] (returns the spoofed locale)
 * - Individual getters such as [Locale.getLanguage], [Locale.getCountry], [Locale.getScript],
 *   [Locale.getDisplayLanguage], [Locale.getDisplayName], etc.
 *
 * If the configured language string is blank, the hook does nothing.
 */
class LocalHook : LoadPackageHandler {

    /**
     * Installs hooks to override all [Locale] query methods with values derived
     * from the configured language string.
     *
     * The language string is split on `"_"` to extract language and optional country
     * codes (e.g., `"zh_CN"` yields language=`"zh"`, country=`"CN"`). If the resulting
     * [Locale] is invalid, the hook silently does nothing via [runCatching].
     */
    override fun onHook() {
        var language = config.language
        var country: String

        // No language configured; nothing to spoof.
        if (language.isBlank()) return

        // Parse "language_country" format, e.g., "en_US" -> language="en", country="US".
        language.split("_").let {
            language = it[0]
            country = if (it.size < 2) "" else it[1]
        }

        // Attempt to create a valid Locale; bail out silently if the tag is malformed.
        runCatching {
            if (country.isBlank()) Locale(language)
            else Locale(language, country)
        }.onSuccess { locale ->
            country = locale.country
            // Pre-compute all display and tag values so hooks return consistent results.
            val displayLanguage = locale.displayLanguage
            val displayCountry = locale.displayCountry
            val displayName = locale.displayName
            val displayVariant = locale.displayVariant
            val displayScript = locale.displayScript
            val script = locale.script
            val variant = locale.variant
            val toLanguageTag = locale.toLanguageTag()
            val toString = locale.toString()

            // Override static and instance Locale methods to return spoofed values.
            Locale::class.java.run {
                setMethodResult("getDefault", locale)
                setMethodResult("getLanguage", language)
                setMethodResult("getCountry", country)
                setMethodResult("getVariant", variant)
                setMethodResult("getScript", script)
                setMethodResult("getDisplayLanguage", displayLanguage)
                setMethodResult("getDisplayCountry", displayCountry)
                setMethodResult("getDisplayName", displayName)
                setMethodResult("getDisplayVariant", displayVariant)
                setMethodResult("getDisplayScript", displayScript)
                setMethodResult("toLanguageTag", toLanguageTag)
                setMethodResult("toString", toString)
            }
        }
    }
}