package com.houvven.ktx_xposed.logger

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database holder for the Xposed module logging subsystem.
 *
 * This class manages the `"module_log.db"` SQLite database and exposes
 * the [ModuleLogDao] for performing CRUD operations on [ModuleLog] entities.
 *
 * The database must be initialised once via [init] before any DAO access.
 * Because Xposed modules run inside the target application's process,
 * [allowMainThreadQueries] is enabled to simplify usage in hook callbacks
 * where a background thread may not be readily available.
 *
 * ### Usage
 * ```kotlin
 * // Initialize once, typically from a ContentProvider's onCreate()
 * ModuleLogDBHelper.init(context)
 *
 * // Access the DAO anywhere after initialization
 * ModuleLogDBHelper.moduleLogDao.insert(someLog)
 * ```
 */
@Database(entities = [ModuleLog::class], version = 1)
abstract class ModuleLogDBHelper : RoomDatabase() {

    /**
     * Returns the DAO instance for accessing [ModuleLog] records.
     *
     * @return The [ModuleLogDao] bound to this database instance.
     */
    abstract fun moduleLogDao(): ModuleLogDao

    companion object {

        /** The underlying Room database instance. Initialized by [init]. */
        private lateinit var db: ModuleLogDBHelper

        /**
         * Shared [ModuleLogDao] instance, available after [init] has been called.
         *
         * Accessing this property before [init] will throw an [UninitializedPropertyAccessException].
         */
        lateinit var moduleLogDao: ModuleLogDao

        /**
         * Initialises the Room database and the shared [moduleLogDao] accessor.
         *
         * This method must be called exactly once before any DAO operations.
         * Subsequent calls will overwrite the existing database and DAO references.
         *
         * @param context The application [Context] used to build the Room database.
         *                Should be the host application's context to ensure correct
         *                database file placement.
         */
        fun init(context: Context) {
            db = Room
                .databaseBuilder(context, ModuleLogDBHelper::class.java, "module_log.db")
                .allowMainThreadQueries()
                .build()
            moduleLogDao = db.moduleLogDao()
        }
    }
}