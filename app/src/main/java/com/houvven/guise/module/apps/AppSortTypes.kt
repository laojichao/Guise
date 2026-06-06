package com.houvven.guise.module.apps

import com.houvven.guise.ContextAmbient
import com.houvven.guise.R

/**
 * Enumerates the available sorting strategies for the installed applications list.
 *
 * Each constant carries a user-facing description string loaded from Android string resources,
 * making it directly usable in UI components such as sort-selection menus.
 *
 * @property depict the localized, human-readable description of this sort type.
 */
enum class AppSortTypes(val depict: String) {
    /** Sort applications alphabetically by their display label. */
    NAME(ContextAmbient.current.getString(R.string.apps_order_by_name)),
    /** Sort applications alphabetically by their package name. */
    PACKAGE_NAME(ContextAmbient.current.getString(R.string.apps_order_by_package_name)),
    /** Sort applications by their first installation time (oldest or newest first). */
    INSTALL_TIME(ContextAmbient.current.getString(R.string.apps_order_by_install_time)),
    /** Sort applications by their last update time (oldest or newest first). */
    UPDATE_TIME(ContextAmbient.current.getString(R.string.apps_order_by_update_time))
}