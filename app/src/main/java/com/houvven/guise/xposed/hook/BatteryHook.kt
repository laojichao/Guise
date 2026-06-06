package com.houvven.guise.xposed.hook

import android.content.Intent
import android.os.BatteryManager
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.afterHookedMethod

/**
 * Xposed hook that spoofs the device battery level reported to the target application.
 *
 * Intercepts two common API paths for reading battery information:
 * - [BatteryManager.getIntProperty] when querying [BatteryManager.BATTERY_PROPERTY_CAPACITY]
 * - [Intent.getIntExtra] when reading [BatteryManager.EXTRA_LEVEL] from a battery status intent
 *
 * The spoofed level is read from [ModuleConfig.batteryLevel]. If the value is -1 (sentinel),
 * the hook does nothing and the original battery level is preserved.
 */
class BatteryHook : LoadPackageHandler {

    /**
     * Installs after-hooks on [BatteryManager] and [Intent] to replace the reported
     * battery capacity with the configured value.
     *
     * The hook is skipped entirely when [config].batteryLevel equals -1, indicating
     * no override is desired.
     *
     * @throws Throwable if the Xposed hook installation fails
     */
    @Throws(Throwable::class)
    override fun onHook() {
        val level = config.batteryLevel
        // Sentinel value -1 means "no override"; leave the real battery level intact.
        if (level == -1) return

        // Hook BatteryManager.getIntProperty(int) to return the spoofed level
        // when the caller requests BATTERY_PROPERTY_CAPACITY.
        BatteryManager::class.java.afterHookedMethod(
            methodName = "getIntProperty", Int::class.java
        ) { param ->
            if (param.args[0] == BatteryManager.BATTERY_PROPERTY_CAPACITY) {
                param.result = level
            }
        }

        // Hook Intent.getIntExtra(String, int) to return the spoofed level
        // when the caller reads EXTRA_LEVEL from a battery status broadcast.
        Intent::class.java.afterHookedMethod(
            methodName = "getIntExtra", String::class.java, Int::class.java
        ) { param ->
            if (param.args[0] == BatteryManager.EXTRA_LEVEL) {
                param.result = level
            }
        }
    }
}