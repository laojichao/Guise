package com.houvven.guise.ui.routing

/**
 * Enumeration of all top-level navigation destinations in the app.
 * Each entry corresponds to a route registered in [NavigationRoute]'s
 * [AnimatedNavHost] and is used as the route identifier string.
 */
enum class NavRoutingTypes {
    /** Main launcher screen showing installed apps and templates. */
    LAUNCHER,

    /** Configuration editor for an individual app's spoofing settings. */
    DEPLOY_CONFIG_EDITOR,

    /** Screen for creating a new spoofing template from scratch. */
    ADD_TEMPLATE,

    /** Screen for editing an existing spoofing template. */
    EDIT_TEMPLATE,

    /** Screen for enabling/assigning a template to a specific app. */
    ENABLE_TEMPLATE,
}