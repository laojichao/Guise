package com.houvven.guise.xposed.config

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.houvven.guise.BuildConfig
import com.houvven.guise.ContextAmbient
import com.houvven.guise.R
import com.houvven.guise.constant.AppConfigKey
import com.houvven.guise.lsposed.LsposedHelper
import com.houvven.guise.module.ktx.runThread
import com.houvven.guise.ui.routing.LauncherState
import com.houvven.guise.xposed.PackageConfig
import com.houvven.ktx_xposed.SafeSharePrefs
import com.houvven.lib.command.ShellActuators

/**
 * Manages the lifecycle of a [ModuleConfig] and its corresponding [ModuleConfigState],
 * providing save, clear, and app-control operations.
 *
 * Acts as the bridge between the UI layer (which edits [ModuleConfigState] via Compose
 * state holders) and the persistence layer (which stores [ModuleConfig] as JSON in
 * SharedPreferences). Use the [companion object][Companion] factory methods to create instances.
 *
 * @property config the [ModuleConfig] being managed; its fields are updated from [state] on save
 * @property state the Compose-observable [ModuleConfigState] that the UI binds to
 */
class ModuleConfigManager
private constructor(
    val config: ModuleConfig,
    val state: ModuleConfigState,
) {

    /** Whether the "super LSPosed" mode is enabled, allowing automatic scope management. */
    private val superLsposed get() = AppConfigKey.run { mmkv.decodeBool(SUPER_LSPOSED, false) }

    /** The module's own application package name. */
    private val modulePkgName = BuildConfig.APPLICATION_ID

    /** SharedPreferences accessor for reading/writing hook configurations. */
    private val safePrefs
        get() = SafeSharePrefs.of(
            ContextAmbient.current,
            PackageConfig.PREF_FILE_NAME
        )

    /** Application context for launching activities and accessing system services. */
    private val context = ContextAmbient.current

    /**
     * Resets the [state] to default (empty) values.
     * Does not persist the change -- call [save] afterwards to persist.
     */
    fun clear() {
        state.clear()
    }

    /**
     * Persists the current configuration.
     *
     * Synchronizes [state] back into [config] via [updateConfigFromState], serializes
     * to JSON, and writes to SharedPreferences. If the configuration is enabled
     * ([ModuleConfig.isEnable]), the entry is stored; otherwise it is removed.
     * When "super LSPosed" mode is active, the target package is also added to or
     * removed from the module's scope via [LsposedHelper].
     */
    fun save() {
        this.updateConfigFromState()
        val json = config.toJson()
        val enable = config.isEnable
        // Update the UI state for the app list
        LauncherState.apps.value.find { it.packageName == config.packageName }?.isEnable = enable
        if (enable) {
            safePrefs.edit { putString(config.packageName, json) }
            // Auto-manage LSPosed scope if super mode is enabled
            if (superLsposed) runThread {
                LsposedHelper.addScope(modulePkgName, config.packageName)
            }
        } else {
            safePrefs.edit(commit = true) { remove(config.packageName) }
            if (superLsposed) runThread {
                LsposedHelper.removeScope(modulePkgName, config.packageName)
            }
        }
    }

    /**
     * Force-stops the target application after saving the configuration.
     *
     * Attempts to stop the app via a root shell command (`am force-stop`). If root
     * access is unavailable, falls back to opening the system "App Info" settings
     * screen so the user can manually force-stop.
     *
     * @return `true` if the root shell command succeeded, `false` if the fallback
     *         intent was launched instead
     */
    fun stopApp(): Boolean {
        this.save()
        var isUseRootSucceed = true
        val result = ShellActuators.exec("am force-stop ${config.packageName}", true)
        result.onFailure {
            isUseRootSucceed = false
            // Fallback: open the system app details screen
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${config.packageName}")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, null)
        }
        return isUseRootSucceed
    }

    /**
     * Force-stops and then relaunches the target application.
     *
     * Saves the configuration, force-stops the app via root shell, and then
     * launches it using its default launch intent. Requires root access.
     *
     * @return [Result.success] if the app was restarted, or [Result.failure] with
     *         a [RuntimeException] if root access is unavailable
     */
    fun restartApp(): Result<Unit> {
        this.save()
        return runCatching {
            ShellActuators.exec("am force-stop ${config.packageName}", true).onFailure {
                throw RuntimeException(context.getString(R.string.no_root_prompt))
            }
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(config.packageName)!!
            ContextCompat.startActivity(context, intent, null)
        }
    }

    /**
     * Reflectively synchronizes all [MutableState] fields in [state] back into
     * the corresponding fields of [config].
     *
     * For each state field, the method finds the matching config field by name,
     * reads the current UI value from the [MutableState], and sets it on the config
     * using the appropriate typed setter. Numeric string values are parsed with
     * fallback to the default config value on parse failure.
     *
     * Supported field types: [Boolean], [String], [Int], [Long], [Short], [Byte],
     * [Double], [Float], [Char].
     */
    fun updateConfigFromState() {
        val empty = ModuleConfig()
        val configFields = config.javaClass.declaredFields.toMutableList()
        val stateFields =
            state.javaClass.declaredFields.filter { it.type == MutableState::class.java }
        for (stateFiled in stateFields) {
            val configField = configFields.find { it.name == stateFiled.name } ?: continue
            val value = (stateFiled.get(state) as MutableState<*>).value
            configField.isAccessible = true

            // Boolean and String are set directly; other types require parsing
            if (configField.type == Boolean::class.java) {
                configField.setBoolean(config, value as Boolean)
                continue
            } else if (configField.type == String::class.java) {
                configField.set(config, value as String)
                continue
            }

            // Parse numeric/primitive types from their String state representation
            value as String
            when (configField.type) {
                Int::class.java -> (value.toIntOrNull() ?: configField.getInt(empty))
                    .let { configField.setInt(config, it) }

                Long::class.java -> (value.toLongOrNull() ?: configField.getLong(empty))
                    .let { configField.setLong(config, it) }

                Short::class.java -> (value.toShortOrNull() ?: configField.getShort(empty))
                    .let { configField.setShort(config, it) }

                Byte::class.java -> (value.toByteOrNull() ?: configField.getByte(empty))
                    .let { configField.setByte(config, it) }

                Double::class.java -> (value.toDoubleOrNull() ?: configField.getDouble(empty))
                    .let { configField.setDouble(config, it) }

                Float::class.java -> (value.toFloatOrNull() ?: configField.getFloat(empty))
                    .let { configField.setFloat(config, it) }

                Char::class.java -> (value.singleOrNull() ?: configField.getChar(empty))
                    .let { configField.setChar(config, it) }

                else -> Unit
            }
        }
    }

    companion object {

        /**
         * Creates a [ModuleConfigManager] from the given [config] and pre-built [state].
         *
         * @param config the [ModuleConfig] to manage
         * @param state the corresponding [ModuleConfigState] for UI binding
         * @return a new [ModuleConfigManager] instance
         */
        fun of(config: ModuleConfig, state: ModuleConfigState) = ModuleConfigManager(config, state)

        /**
         * Creates a [ModuleConfigManager] from the given [config], automatically
         * deriving a fresh [ModuleConfigState] from it.
         *
         * @param config the [ModuleConfig] to manage
         * @return a new [ModuleConfigManager] instance with an auto-generated state
         */
        fun of(config: ModuleConfig): ModuleConfigManager {
            val state = ModuleConfigState.of(config)
            return ModuleConfigManager(config, state)
        }

        /**
         * Creates a [ModuleConfigManager] with a default (empty) configuration.
         *
         * @return a new [ModuleConfigManager] instance with all default values
         */
        fun empty() = of(ModuleConfig())
    }


}