@file:Suppress("unused")

package com.houvven.ktx_xposed.hook

import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge


/**
 * Constants representing the hook execution phase relative to the target method invocation.
 */
object HookType {
    /** Hook fires before the original method executes. */
    const val BEFORE = 0
    /** Hook fires after the original method executes. */
    const val AFTER = 1
}


// ==================== Before/After Hook by Class Name ====================

/**
 * Hooks a method by class name to execute [callback] **before** the original method runs.
 * The target class is resolved via [findClass].
 *
 * @param className the fully qualified name of the class containing the target method
 * @param methodName the name of the method to hook
 * @param parameterTypes the parameter types to match the method signature
 * @param callback the lambda invoked with the [MethodHookParam] before the original method
 */
inline fun beforeHookedMethod(
    className: String,
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).beforeHookedMethod(methodName, *parameterTypes, callback = callback)
}

/**
 * Hooks a method on this [Class] to execute [callback] **before** the original method runs.
 * Internally creates an anonymous [XC_MethodHook] that delegates to the provided lambda.
 *
 * @param methodName the name of the method to hook
 * @param parameterTypes the parameter types to match the method signature
 * @param callback the lambda invoked with the [MethodHookParam] before the original method
 */
inline fun Class<*>.beforeHookedMethod(
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    hookMethod(this, methodName, *parameterTypes, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            callback(param)
        }
    })
}

/**
 * Hooks a method by class name to execute [callback] **after** the original method runs.
 * The target class is resolved via [findClass].
 *
 * @param className the fully qualified name of the class containing the target method
 * @param methodName the name of the method to hook
 * @param parameterTypes the parameter types to match the method signature
 * @param callback the lambda invoked with the [MethodHookParam] after the original method
 */
inline fun afterHookedMethod(
    className: String,
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).afterHookedMethod(methodName, *parameterTypes, callback = callback)
}

/**
 * Hooks a method on this [Class] to execute [callback] **after** the original method runs.
 * Internally creates an anonymous [XC_MethodHook] that delegates to the provided lambda.
 *
 * @param methodName the name of the method to hook
 * @param parameterTypes the parameter types to match the method signature
 * @param callback the lambda invoked with the [MethodHookParam] after the original method
 */
inline fun Class<*>.afterHookedMethod(
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    hookMethod(this, methodName, *parameterTypes, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            callback(param)
        }
    })
}

// ==================== Before/After Hook All Methods ====================

/**
 * Hooks **all** overloaded methods with the given name by class name to execute [callback]
 * **before** each method runs. Uses [hookAllMethods] internally.
 *
 * @param className the fully qualified name of the class
 * @param methodName the name of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] before each method
 */
inline fun beforeHookAllMethods(
    className: String,
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).beforeHookAllMethods(methodName, callback = callback)
}

/**
 * Hooks **all** overloaded methods with the given name on this [Class] to execute [callback]
 * **before** each method runs.
 *
 * @param methodName the name of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] before each method
 * @return a [Set] of [XC_MethodHook.Unhook] objects for all hooked overloads
 */
inline fun Class<*>.beforeHookAllMethods(
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = hookAllMethods(this, methodName, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})


/**
 * Hooks **all** overloaded methods with the given name by class name to execute [callback]
 * **after** each method runs.
 *
 * @param className the fully qualified name of the class
 * @param methodName the name of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] after each method
 */
inline fun afterHookAllMethods(
    className: String,
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).afterHookAllMethods(methodName, callback = callback)
}

/**
 * Hooks **all** overloaded methods with the given name on this [Class] to execute [callback]
 * **after** each method runs.
 *
 * @param methodName the name of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] after each method
 * @return a [Set] of [XC_MethodHook.Unhook] objects for all hooked overloads
 */
inline fun Class<*>.afterHookAllMethods(
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = hookAllMethods(this, methodName, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})

// ==================== Before/After Hook Constructor ====================

/**
 * Hooks a constructor by class name to execute [callback] **before** the constructor runs.
 * The target class is resolved via [findClass].
 *
 * @param className the fully qualified name of the class
 * @param parameterTypes the parameter types to match the constructor signature
 * @param callback the lambda invoked with the [MethodHookParam] before the constructor
 */
inline fun beforeHookConstructor(
    className: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).beforeHookConstructor(*parameterTypes, callback = callback)
}

/**
 * Hooks a constructor on this [Class] to execute [callback] **before** the constructor runs.
 *
 * @param parameterTypes the parameter types to match the constructor signature
 * @param callback the lambda invoked with the [MethodHookParam] before the constructor
 */
inline fun Class<*>.beforeHookConstructor(
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = hookConstructor(this, *parameterTypes, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})

/**
 * Hooks a constructor by class name to execute [callback] **after** the constructor runs.
 * The target class is resolved via [findClass].
 *
 * @param className the fully qualified name of the class
 * @param parameterTypes the parameter types to match the constructor signature
 * @param callback the lambda invoked with the [MethodHookParam] after the constructor
 */
inline fun afterHookConstructor(
    className: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).afterHookConstructor(*parameterTypes, callback = callback)
}

/**
 * Hooks a constructor on this [Class] to execute [callback] **after** the constructor runs.
 *
 * @param parameterTypes the parameter types to match the constructor signature
 * @param callback the lambda invoked with the [MethodHookParam] after the constructor
 */
inline fun Class<*>.afterHookConstructor(
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = hookConstructor(this, *parameterTypes, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})

// ==================== Before/After Hook All Constructors ====================

/**
 * Hooks **all** constructors by class name to execute [callback] **before** each runs.
 * The target class is resolved via [findClass].
 *
 * @param className the fully qualified name of the class
 * @param callback the lambda invoked with the [MethodHookParam] before each constructor
 */
inline fun beforeHookAllConstructors(
    className: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).beforeHookAllConstructors(callback = callback)
}

/**
 * Hooks **all** constructors on this [Class] to execute [callback] **before** each runs.
 *
 * @param callback the lambda invoked with the [MethodHookParam] before each constructor
 * @return a [Set] of [XC_MethodHook.Unhook] objects for all hooked constructors
 */
inline fun Class<*>.beforeHookAllConstructors(
    crossinline callback: (MethodHookParam) -> Unit,
) = hookAllConstructors(this, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})

/**
 * Hooks **all** constructors by class name to execute [callback] **after** each runs.
 *
 * @param className the fully qualified name of the class
 * @param callback the lambda invoked with the [MethodHookParam] after each constructor
 */
inline fun afterHookAllConstructors(
    className: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).afterHookAllConstructors(callback = callback)
}

/**
 * Hooks **all** constructors on this [Class] to execute [callback] **after** each runs.
 *
 * @param callback the lambda invoked with the [MethodHookParam] after each constructor
 * @return a [Set] of [XC_MethodHook.Unhook] objects for all hooked constructors
 */
inline fun Class<*>.afterHookAllConstructors(
    crossinline callback: (MethodHookParam) -> Unit,
) = hookAllConstructors(this, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})

// ==================== Multi-class Same-name Method Hooks ====================

/**
 * Hooks methods with the same name across **multiple different classes** to execute [callback]
 * **before** each method runs. Each entry in [classAndMethodName] specifies a class and the
 * method name to hook within it.
 *
 * @param classAndMethodName list of pairs where [Pair.first] is the target class and
 *        [Pair.second] is the method name to hook
 * @param callback the lambda invoked with the [MethodHookParam] before each method
 * @return a [Map] from each class to its set of [XC_MethodHook.Unhook] objects
 */
inline fun beforeHookSomeSameNameMethodForAnyClass(
    classAndMethodName: List<Pair<Class<*>, String>>,
    crossinline callback: (MethodHookParam) -> Unit,
) = classAndMethodName.let {
    val map = mutableMapOf<Class<*>, MutableSet<XC_MethodHook.Unhook?>>()
    it.forEach { (clazz, methodName) ->
        val unhooks = clazz.beforeHookAllMethods(methodName, callback = callback)
        map[clazz] = unhooks
    }
    map
}

/**
 * Hooks methods with the same name across **multiple different classes** to execute [callback]
 * **after** each method runs.
 *
 * @param classAndMethodName list of pairs where [Pair.first] is the target class and
 *        [Pair.second] is the method name to hook
 * @param callback the lambda invoked with the [MethodHookParam] after each method
 * @return a [Map] from each class to its set of [XC_MethodHook.Unhook] objects
 */
inline fun afterHookSomeSameNameMethodForAnyClass(
    classAndMethodName: List<Pair<Class<*>, String>>,
    crossinline callback: (MethodHookParam) -> Unit,
) = classAndMethodName.run {
    val map = mutableMapOf<Class<*>, MutableSet<XC_MethodHook.Unhook?>>()
    forEach { (clazz, methodName) ->
        val unhooks = clazz.afterHookAllMethods(methodName, callback = callback)
        map[clazz] = unhooks
    }
    map
}

/**
 * Hooks multiple methods by name within a single class (resolved by [className]) to execute
 * [callback] **before** each method runs.
 *
 * @param className the fully qualified name of the class
 * @param methodName the names of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] before each method
 * @return a [Set] of [XC_MethodHook.Unhook] results
 */
inline fun beforeHookSomeSameNameMethod(
    className: String,
    vararg methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = methodName.run {
    map {
        beforeHookAllMethods(className, it, callback = callback)
    }.toSet()
}

/**
 * Hooks multiple methods by name on this [Class] to execute [callback] **before** each method runs.
 *
 * @param methodName the names of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] before each method
 * @return a [Set] of results from each hook registration
 */
inline fun Class<*>.beforeHookSomeSameNameMethod(
    vararg methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = methodName.run {
    map {
        this@beforeHookSomeSameNameMethod.beforeHookAllMethods(it, callback = callback)
    }.toSet()
}

/**
 * Hooks multiple methods by name within a single class (resolved by [className]) to execute
 * [callback] **after** each method runs.
 *
 * @param className the fully qualified name of the class
 * @param methodName the names of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] after each method
 * @return a [Set] of [XC_MethodHook.Unhook] results
 */
inline fun afterHookSomeSameNameMethod(
    className: String,
    vararg methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = methodName.run {
    map {
        afterHookAllMethods(className, it, callback = callback)
    }.toSet()
}

/**
 * Hooks multiple methods by name on this [Class] to execute [callback] **after** each method runs.
 *
 * @param methodName the names of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] after each method
 * @return a [Set] of results from each hook registration
 */
inline fun Class<*>.afterHookSomeSameNameMethod(
    vararg methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = methodName.run {
    map {
        this@afterHookSomeSameNameMethod.afterHookAllMethods(it, callback = callback)
    }.toSet()
}

// ==================== Method Replacement ====================

/**
 * Replaces a method entirely so that the original method body is never executed.
 * The [callback] lambda's return value is used as the method result.
 * Uses [XC_MethodReplacement] under the hood.
 *
 * @param clazz the class containing the target method
 * @param methodName the name of the method to replace
 * @param parameterTypes the parameter types to match the method signature
 * @param callback the lambda that produces the replacement return value from the [MethodHookParam]
 */
inline fun replaceMethod(
    clazz: Class<*>,
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Any?,
) = hookMethod(clazz, methodName, *parameterTypes, object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        return callback(param)
    }
})


// ==================== Reified Type Extension Functions ====================

/**
 * Hooks a method on the reified type [T] to execute [callback] **before** the original method runs.
 *
 * @param T the class containing the target method
 * @param methodName the name of the method to hook
 * @param parameterTypes the parameter types to match the method signature
 * @param callback the lambda invoked with the [MethodHookParam] before the original method
 */
inline fun <reified T> beforeHookedMethod(
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.beforeHookedMethod(methodName, *parameterTypes, callback = callback)

/**
 * Hooks a method on the reified type [T] to execute [callback] **after** the original method runs.
 *
 * @param T the class containing the target method
 * @param methodName the name of the method to hook
 * @param parameterTypes the parameter types to match the method signature
 * @param callback the lambda invoked with the [MethodHookParam] after the original method
 */
inline fun <reified T> afterHookedMethod(
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.afterHookedMethod(methodName, *parameterTypes, callback = callback)

/**
 * Hooks **all** overloaded methods with the given name on the reified type [T] to execute
 * [callback] **before** each method runs.
 *
 * @param T the class containing the target methods
 * @param methodName the name of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] before each method
 */
inline fun <reified T> beforeHookAllMethods(
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.beforeHookAllMethods(methodName, callback = callback)

/**
 * Hooks **all** overloaded methods with the given name on the reified type [T] to execute
 * [callback] **after** each method runs.
 *
 * @param T the class containing the target methods
 * @param methodName the name of the methods to hook
 * @param callback the lambda invoked with the [MethodHookParam] after each method
 */
inline fun <reified T> afterHookAllMethods(
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.afterHookAllMethods(methodName, callback = callback)

/**
 * Hooks a constructor on the reified type [T] to execute [callback] **before** it runs.
 *
 * @param T the class containing the target constructor
 * @param parameterTypes the parameter types to match the constructor signature
 * @param callback the lambda invoked with the [MethodHookParam] before the constructor
 */
inline fun <reified T> beforeHookConstructor(
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.beforeHookConstructor(*parameterTypes, callback = callback)

/**
 * Hooks a constructor on the reified type [T] to execute [callback] **after** it runs.
 *
 * @param T the class containing the target constructor
 * @param parameterTypes the parameter types to match the constructor signature
 * @param callback the lambda invoked with the [MethodHookParam] after the constructor
 */
inline fun <reified T> afterHookConstructor(
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.afterHookConstructor(*parameterTypes, callback = callback)

/**
 * Hooks **all** constructors on the reified type [T] to execute [callback] **before** each runs.
 *
 * @param T the class whose constructors to hook
 * @param callback the lambda invoked with the [MethodHookParam] before each constructor
 */
inline fun <reified T> beforeHookAllConstructors(
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.beforeHookAllConstructors(callback = callback)

/**
 * Hooks **all** constructors on the reified type [T] to execute [callback] **after** each runs.
 *
 * @param T the class whose constructors to hook
 * @param callback the lambda invoked with the [MethodHookParam] after each constructor
 */
inline fun <reified T> afterHookAllConstructors(
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.afterHookAllConstructors(callback = callback)

// ==================== Set Method Result Shortcuts ====================

/**
 * Overrides the return value of a method by class name. Sets [MethodHookParam.result]
 * in the specified [type] phase (before or after the original method).
 *
 * @param className the fully qualified name of the class containing the target method
 * @param methodName the name of the method whose result should be overridden
 * @param value the value to set as the method's return result
 * @param type the hook phase in which to set the result: [HookType.BEFORE] or [HookType.AFTER]
 * @param parameterTypes the parameter types to match the method signature
 */
fun setMethodResult(
    className: String,
    methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
    vararg parameterTypes: Class<*>,
) = runXposedCatching {
    findClass(className).setMethodResult(methodName, value, type, *parameterTypes)
}

/**
 * Overrides the return value of a method on this [Class]. Sets [MethodHookParam.result]
 * in the specified [type] phase (before or after the original method).
 *
 * @param methodName the name of the method whose result should be overridden
 * @param value the value to set as the method's return result
 * @param type the hook phase in which to set the result: [HookType.BEFORE] or [HookType.AFTER]
 * @param parameterTypes the parameter types to match the method signature
 */
fun Class<*>.setMethodResult(
    methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
    vararg parameterTypes: Class<*>,
) = this.run {
    if (type == HookType.BEFORE)
        beforeHookedMethod(methodName, *parameterTypes) { it.result = value }
    else
        afterHookedMethod(methodName, *parameterTypes) { it.result = value }
}

/**
 * Overrides the return value of **all** overloaded methods with the given name by class name.
 *
 * @param className the fully qualified name of the class
 * @param methodName the name of the methods whose results should be overridden
 * @param value the value to set as the methods' return result
 * @param type the hook phase in which to set the result: [HookType.BEFORE] or [HookType.AFTER]
 */
fun setAllMethodResult(
    className: String,
    methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
) = runXposedCatching {
    findClass(className).setAllMethodResult(methodName, value, type)
}

/**
 * Overrides the return value of **all** overloaded methods with the given name on this [Class].
 *
 * @param methodName the name of the methods whose results should be overridden
 * @param value the value to set as the methods' return result
 * @param type the hook phase in which to set the result: [HookType.BEFORE] or [HookType.AFTER]
 */
fun Class<*>.setAllMethodResult(
    methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
) = this.run {
    if (type == 0) beforeHookAllMethods(methodName) { it.result = value }
    else afterHookAllMethods(methodName) { it.result = value }
}

/**
 * Overrides the return value of same-named methods across **multiple different classes**.
 *
 * @param classAndMethodName list of pairs where [Pair.first] is the target class and
 *        [Pair.second] is the method name
 * @param value the value to set as the methods' return result
 * @param type the hook phase in which to set the result: [HookType.BEFORE] or [HookType.AFTER]
 * @return a [Map] from each class to its set of [XC_MethodHook.Unhook] objects
 */
fun setSomeSameNameMethodResultForAnyClass(
    classAndMethodName: List<Pair<Class<*>, String>>,
    value: Any?,
    type: Int = HookType.BEFORE,
) = if (type == HookType.BEFORE)
    beforeHookSomeSameNameMethodForAnyClass(classAndMethodName) { it.result = value }
else
    afterHookSomeSameNameMethodForAnyClass(classAndMethodName) { it.result = value }


/**
 * Overrides the return value of multiple named methods on this [Class].
 *
 * @param methodName the names of the methods whose results should be overridden
 * @param value the value to set as the methods' return result
 * @param type the hook phase in which to set the result: [HookType.BEFORE] or [HookType.AFTER]
 */
fun Class<*>.setSomeSameNameMethodResult(
    vararg methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
) = runXposedCatching {
    if (type == HookType.BEFORE)
        this.beforeHookSomeSameNameMethod(*methodName) { it.result = value }
    else
        this.afterHookSomeSameNameMethod(*methodName) { it.result = value }
}

