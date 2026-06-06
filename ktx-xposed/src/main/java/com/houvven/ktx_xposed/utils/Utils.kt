package com.houvven.ktx_xposed.utils

import de.robv.android.xposed.XC_MethodHook.MethodHookParam

/**
 * Sets the hooked method's return value to `null`.
 *
 * Typically used in an `afterHookedMethod` callback to forcibly override
 * the original method's result with a null value, effectively short-circuiting
 * any downstream logic that depends on a non-null return.
 *
 * @receiver The current [MethodHookParam] context inside the hook callback.
 */
fun MethodHookParam.setNullResult() {
    result = null
}

/**
 * Checks whether any element in the hook's [MethodHookParam.args] array
 * is an instance of the exact type [type] (not a subtype).
 *
 * This is useful when a hooked method accepts multiple overloads and you
 * need to determine which overload was invoked by inspecting the runtime
 * types of the arguments.
 *
 * @param type The exact [Class] to match against each argument's runtime type.
 *             Subclasses are **not** considered matches.
 * @return `true` if at least one argument's [Class] is exactly [type];
 *         `false` otherwise, or if [MethodHookParam.args] is empty.
 */
fun MethodHookParam.hasTypeArg(type: Class<*>): Boolean {
    return args.any { it.javaClass == type }
}

/**
 * Returns the index of the first argument in [MethodHookParam.args] whose
 * runtime type is exactly [type] (not a subtype).
 *
 * Combined with [hasTypeArg], this lets you retrieve a specific typed argument
 * without relying on positional indices that may differ across overloads.
 *
 * @param type The exact [Class] to match against each argument's runtime type.
 * @return The zero-based index of the first matching argument, or `-1` if no
 *         argument matches the given [type].
 */
fun MethodHookParam.getTypeArgIndexOfFirst(type: Class<*>): Int {
    return args.indexOfFirst { it.javaClass == type }
}
