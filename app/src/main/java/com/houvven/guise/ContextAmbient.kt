package com.houvven.guise

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.houvven.guise.lsposed.LsposedHelper
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import java.io.File

/**
 * Custom [Application] subclass that serves as the central context provider for the app.
 *
 * On creation, it stores the application context in a static field for global access,
 * initializes MMKV key-value storage (with logging disabled), and sets up the LsposedHelper
 * with a bundled SQLite binary used by the Xposed/LSPosed module infrastructure.
 */
class ContextAmbient : Application() {

    companion object {
        /**
         * Global application context reference, available after [onCreate] completes.
         *
         * Annotated with [SuppressLint] to suppress the static field leak warning,
         * since this intentionally holds the application context (not an Activity).
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var current: Context

        /**
         * Returns a [SharedPreferences][android.content.SharedPreferences] instance
         * associated with the given [name].
         *
         * @param name the preferences file name, defaults to [BuildConfig.APPLICATION_ID].
         * @param mode the file creation mode, defaults to [Context.MODE_PRIVATE].
         * @return the [SharedPreferences][android.content.SharedPreferences] instance.
         */
        fun getSharedPreferences(
            name: String = BuildConfig.APPLICATION_ID,
            mode: Int = Context.MODE_PRIVATE,
        ) = current.getSharedPreferences(name, mode)
    }

    override fun onCreate() {
        super.onCreate()
        current = applicationContext
        // Initialize MMKV with all logging suppressed
        MMKV.initialize(this, MMKVLogLevel.LevelNone)
        // Initialize LsposedHelper pointing to the bundled sqlite3 binary in app-private storage
        LsposedHelper.init(File(filesDir, "/bin/sqlite3").absolutePath)
    }


}