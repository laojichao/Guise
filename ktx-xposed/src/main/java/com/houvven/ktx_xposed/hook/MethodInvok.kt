package com.houvven.ktx_xposed.hook

import java.lang.reflect.Method

/**
 * Overrides the return value of this [Method] by delegating to [Class.setMethodResult]
 * on the method's declaring class. This is a convenience extension that avoids having to
 * manually reference the declaring class and method name separately.
 *
 * @param value the value to set as the method's return result
 * @param type the hook phase in which to set the result: [HookType.BEFORE] or [HookType.AFTER]
 */
fun Method.setMethodResult(value: Any?, type: Int = HookType.BEFORE) {
    this.declaringClass.setMethodResult(this.name, value, type)
}

