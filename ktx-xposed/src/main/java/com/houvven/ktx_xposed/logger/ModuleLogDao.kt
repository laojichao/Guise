package com.houvven.ktx_xposed.logger

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Data Access Object (DAO) for the [ModuleLog] entity.
 *
 * Provides basic CRUD operations against the `"module_log"` table managed by Room.
 * All methods execute synchronously; callers are responsible for threading.
 */
@Dao
interface ModuleLogDao {

    /**
     * Retrieves every log entry stored in the database, ordered by insertion order.
     *
     * @return A [List] of all [ModuleLog] records. Returns an empty list when no entries exist.
     */
    @Query("SELECT * FROM module_log")
    fun getAll(): List<ModuleLog>

    /**
     * Persists a single [ModuleLog] entry into the database.
     *
     * @param moduleLog The log entry to insert. The [ModuleLog.id] field is auto-generated
     *                  and will be ignored if provided.
     */
    @Insert
    fun insert(moduleLog: ModuleLog)

    /**
     * Deletes all log entries from the `"module_log"` table.
     *
     * This operation is irreversible.
     */
    @Query("DELETE FROM module_log")
    fun clearAll()

}