package com.houvven.guise.xposed.other

import android.app.Application
import android.content.Context
import com.houvven.guise.module.ktx.showToast
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.afterHookedMethod

/**
 * Xposed hook that displays a toast notification in the target application
 * to confirm that the Guise module's hooks have been successfully applied.
 *
 * When [config.hookSuccessHint] is `true`, this hook intercepts
 * [Application.attach] and shows a toast message ("Guise's hook success prompt")
 * using the application's [Context]. This provides immediate visual feedback
 * to the user that the module is active and functioning in the target process.
 *
 * The toast is displayed as early as possible in the application lifecycle
 * ([Application.attach] is one of the first callbacks invoked) to give
 * prompt confirmation.
 */
class HookSuccessHint : LoadPackageHandler {

    /**
     * Entry point for the success hint hook.
     *
     * If [config.hookSuccessHint] is `false`, this method returns immediately
     * without installing any hooks. Otherwise, it hooks [Application.attach]
     * to show a confirmation toast when the target app starts.
     */
    override fun onHook() {
        if (!config.hookSuccessHint) return
        Application::class.java.afterHookedMethod(
            methodName = "attach",
            Context::class.java
        ) { param ->
            val context = param.args[0] as Context
            context.showToast("Guise's hook success prompt")
        }
    }
}
