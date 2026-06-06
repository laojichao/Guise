package com.houvven.ktx_xposed

/**
 * Adapter interface for individual Xposed hook implementations within a module.
 *
 * Each concrete implementation of this interface encapsulates a single, self-contained
 * hooking unit (e.g., spoofing device information, bypassing root detection). Implementations
 * are collected into a list and executed by [com.houvven.ktx_xposed.handler.HookLoadPackageHandler.doHookLoadPackage],
 * which iterates over all adapters and calls [onHook] on each one with exception safety.
 *
 * ## Usage
 * ```kotlin
 * class SpoofBuildModel : LoadPackageHookAdapter {
 *     override fun onHook() {
 *         // Hook Build.MODEL or related methods here
 *     }
 * }
 * ```
 */
interface LoadPackageHookAdapter {

    /**
     * Executes the hook logic for this adapter.
     *
     * This method is called by the hook handler framework after the target package
     * has been loaded. Implementations should register all necessary Xposed hooks
     * (method hooks, constructor hooks, field hooks, etc.) within this method.
     *
     * Exceptions thrown inside this method are caught and logged by the caller
     * via [com.houvven.ktx_xposed.utils.runXposedCatching], ensuring that a failure
     * in one adapter does not prevent other adapters from executing.
     */
    fun onHook();

}
