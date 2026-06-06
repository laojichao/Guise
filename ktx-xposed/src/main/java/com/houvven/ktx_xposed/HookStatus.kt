package com.houvven.ktx_xposed

/**
 * Provides a runtime check to determine whether the Xposed module is currently activated
 * (i.e., loaded and running within a hooked application process).
 *
 * The [isActivated] method initially returns `false`. At runtime, the module's entry point
 * hooks this method via [com.houvven.ktx_xposed.hook.setMethodResult] to return `true`,
 * signaling that the Xposed framework has successfully loaded the module into the target process.
 *
 * This class serves as the canonical activation indicator for the entire module. Other components
 * such as [com.houvven.ktx_xposed.SafeSharePrefs] rely on [isActivated] to decide whether
 * cross-process SharedPreferences access (via `MODE_WORLD_READABLE`) is available.
 */
class HookStatus {
    companion object {
        /**
         * Checks whether the Xposed module is activated in the current process.
         *
         * This method returns `false` by default. During module initialization, the Xposed
         * entry point hooks this method to unconditionally return `true`, which allows
         * downstream code to detect the activated state at runtime.
         *
         * @return `true` if the module is activated and running inside a hooked process;
         *         `false` otherwise (default value before hook is applied).
         */
        fun isActivated(): Boolean {
            return false
        }
    }
}
