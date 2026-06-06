package com.houvven.guise.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Room DAO for CRUD operations on [Template] entities.
 *
 * All methods execute synchronously on the calling thread. The database is
 * configured with [allowMainThreadQueries][androidx.room.RoomDatabase.Builder.allowMainThreadQueries]
 * so these may be called from the UI thread in simple scenarios, but callers
 * are encouraged to move heavy operations to a background dispatcher.
 */
@Dao
interface TemplateDao {

    /**
     * Retrieves all templates stored in the database.
     *
     * @return A list of every [Template] entity, ordered by insertion time.
     */
    @Query("SELECT * FROM Template")
    fun getAll(): List<Template>

    /**
     * Inserts a single [Template] into the database.
     *
     * @param template The template to insert.
     * @throws android.database.sqlite.SQLiteConstraintException if a template
     *         with the same primary key already exists ([OnConflictStrategy.ABORT]).
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(template: Template)

    /**
     * Inserts a batch of templates, silently skipping any that already exist.
     *
     * @param templates The list of templates to insert.
     * @return A list of row IDs for the newly inserted templates. Entries that
     *         conflicted will have an ID of `-1`.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMany(templates: List<Template>): List<Long>

    /**
     * Updates an existing [Template] matched by its primary key.
     *
     * @param template The template with updated fields.
     */
    @Update
    fun update(template: Template)

    /**
     * Deletes a single [Template] from the database.
     *
     * @param template The template to delete (matched by primary key).
     */
    @Delete
    fun delete(template: Template)

    /**
     * Deletes multiple templates in a single transaction.
     *
     * @param templates The list of templates to delete.
     */
    @Delete
    fun deleteMany(templates: List<Template>)

    /**
     * Deletes multiple templates specified as varargs in a single transaction.
     *
     * @param templates The templates to delete.
     */
    @Delete
    fun deleteMany(vararg templates: Template)
}