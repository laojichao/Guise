package com.houvven.guise.ui.routing

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import com.houvven.guise.db.Template
import com.houvven.guise.db.TemplateDBHelper
import com.houvven.guise.module.apps.AppInfo
import com.houvven.guise.module.apps.AppInfoProvider

/**
 * Singleton that holds the launcher screen's observable state for installed applications
 * and spoofing templates. Acts as the single source of truth for data displayed on the
 * main launcher UI and provides mutation methods that automatically refresh the state
 * after database operations.
 */
@SuppressLint("MutableCollectionMutableState")
object LauncherState {

    private val templateDao = TemplateDBHelper.templateDao

    /**
     * Observable list of all installed application info, loaded from the device.
     * Refreshed via [refreshApps].
     */
    val apps = mutableStateOf<List<AppInfo>>(emptyList())

    /**
     * Observable list of all user-created spoofing templates, loaded lazily from the
     * local database on first access and refreshed after any write operation.
     */
    val templates by lazy { mutableStateOf(templateDao.getAll()) }

    /**
     * Reloads the list of installed applications from [AppInfoProvider] into [apps].
     */
    fun refreshApps() {
        apps.value = AppInfoProvider.getList()
    }

    /**
     * Reloads all templates from the database into [templates].
     */
    private fun refreshTemplates() {
        templates.value = templateDao.getAll()
    }

    /**
     * Persists a new [template] to the database and refreshes the observable template list.
     *
     * @param template the [Template] to insert.
     */
    fun addTemplate(template: Template) {
        templateDao.insert(template)
        refreshTemplates()
    }

    /**
     * Persists multiple [templates] in a single batch and refreshes the observable list.
     *
     * @param templates the list of [Template] objects to insert.
     */
    fun addTemplates(templates: List<Template>) {
        templateDao.insertMany(templates)
        refreshTemplates()
    }

    /**
     * Removes the given [template] from the database and refreshes the observable list.
     *
     * @param template the [Template] to delete.
     */
    fun deleteTemplate(template: Template) {
        templateDao.delete(template)
        refreshTemplates()
    }

    /**
     * Updates an existing [template] in the database and refreshes the observable list.
     *
     * @param template the [Template] with updated fields to persist.
     */
    fun updateTemplate(template: Template) {
        templateDao.update(template)
        refreshTemplates()
    }


}