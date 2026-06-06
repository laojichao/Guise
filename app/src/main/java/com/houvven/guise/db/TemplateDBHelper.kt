package com.houvven.guise.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.houvven.guise.ContextAmbient

/**
 * Room database definition for persisting [Template] entities.
 *
 * The database is lazily initialized as a singleton via the [companion object][Companion].
 * Main-thread queries are allowed for simplicity, and destructive migration is
 * enabled so the database is recreated from scratch when the schema version
 * changes (template data is considered non-critical and easily re-created).
 */
@Database(entities = [Template::class], version = 3)
abstract class TemplateDBHelper : RoomDatabase() {

    /**
     * Provides access to the [TemplateDao] for performing CRUD operations.
     *
     * @return The DAO instance bound to this database.
     */
    abstract fun templateDao(): TemplateDao

    companion object {
        /** Singleton database instance, created lazily on first access. */
        private val db: TemplateDBHelper by lazy {
            Room.databaseBuilder(
                ContextAmbient.current,
                TemplateDBHelper::class.java,
                "template.db"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }

        /**
         * Singleton [TemplateDao] shortcut.
         *
         * Accessing this property implicitly opens the database if it has not
         * been opened yet.
         */
        val templateDao by lazy { db.templateDao() }

    }
}