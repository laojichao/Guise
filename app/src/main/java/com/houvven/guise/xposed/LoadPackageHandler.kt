package com.houvven.guise.xposed

import com.houvven.ktx_xposed.LoadPackageHookAdapter

/**
 * Common interface for individual Xposed hook handlers that target specific package behaviors.
 *
 * Extends [LoadPackageHookAdapter] and provides convenient access to the current
 * [ModuleConfig] via the [config] property, which delegates to [PackageConfig.current].
 * All feature-specific hook classes (e.g., device identity, network, location) should
 * implement this interface to get a unified configuration access point.
 */
interface LoadPackageHandler : LoadPackageHookAdapter {

    /**
     * The current module configuration for the loaded package.
     *
     * Defaults to [PackageConfig.current], which is refreshed each time a new
     * package is loaded by the Xposed framework.
     */
    val config get() = PackageConfig.current
}