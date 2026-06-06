package com.houvven.ktx_xposed.hook

import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.XposedHelpers


/**
 * Invokes a static method on this [Class] by name using [XposedHelpers.callStaticMethod].
 * The method is resolved dynamically at runtime by name and arguments.
 *
 * @param methodName the name of the static method to invoke
 * @param args the arguments to pass to the method
 * @return the return value of the invoked method, or `null` if an error occurs
 * @throws NoSuchMethodError if the method cannot be found on the class
 * @throws XposedHelpers.InvocationTargetError if the method throws an exception during invocation
 */
@Throws(
    NoSuchMethodError::class,
    XposedHelpers.InvocationTargetError::class
)
fun Class<*>.callStaticMethod(
    methodName: String, vararg args: Any?
): Any? = runXposedCatching { XposedHelpers.callStaticMethod(this, methodName, *args) }


/**
 * Invokes a static method on this [Class] by name with explicit parameter types
 * using [XposedHelpers.callStaticMethod].
 * Use this overload when method resolution is ambiguous due to overloaded methods.
 *
 * @param methodName the name of the static method to invoke
 * @param parameterTypes the array of parameter types to match the exact method signature
 * @param args the arguments to pass to the method
 * @return the return value of the invoked method, or `null` if an error occurs
 * @throws NoSuchMethodError if the method cannot be found on the class
 * @throws XposedHelpers.InvocationTargetError if the method throws an exception during invocation
 */
@Throws(
    NoSuchMethodError::class,
    XposedHelpers.InvocationTargetError::class
)
fun Class<*>.callStaticMethod(
    methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any?
): Any? =
    runXposedCatching { XposedHelpers.callStaticMethod(this, methodName, parameterTypes, *args) }


/**
 * Invokes an instance method on this object by name using [XposedHelpers.callMethod].
 * The method is resolved dynamically at runtime by name and arguments.
 *
 * @param methodName the name of the instance method to invoke
 * @param args the arguments to pass to the method
 * @return the return value of the invoked method, or `null` if an error occurs
 * @throws NoSuchMethodError if the method cannot be found on the object's class
 * @throws XposedHelpers.InvocationTargetError if the method throws an exception during invocation
 */
@Throws(
    NoSuchMethodError::class,
    XposedHelpers.InvocationTargetError::class
)
fun Any.callMethod(
    methodName: String, vararg args: Any?
): Any? = runXposedCatching { XposedHelpers.callMethod(this, methodName, *args) }


/**
 * Invokes an instance method on this object by name with explicit parameter types
 * using [XposedHelpers.callMethod].
 * Use this overload when method resolution is ambiguous due to overloaded methods.
 *
 * @param methodName the name of the instance method to invoke
 * @param parameterTypes the array of parameter types to match the exact method signature
 * @param args the arguments to pass to the method
 * @return the return value of the invoked method, or `null` if an error occurs
 * @throws NoSuchMethodError if the method cannot be found on the object's class
 * @throws XposedHelpers.InvocationTargetError if the method throws an exception during invocation
 */
@Throws(
    NoSuchMethodError::class,
    XposedHelpers.InvocationTargetError::class
)
fun Any.callMethod(
    methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any?
): Any? = runXposedCatching { XposedHelpers.callMethod(this, methodName, parameterTypes, *args) }


// "IfExists" variants: silently return null when the method does not exist


/**
 * Invokes an instance method on this object only if it exists, returning `null` if the
 * method is not found or throws an exception. Unlike [callMethod], this variant does not
 * propagate errors, making it safe for optional method invocations.
 *
 * @param methodName the name of the instance method to invoke
 * @param args the arguments to pass to the method
 * @return the return value of the invoked method, or `null` if the method does not exist or fails
 */
fun Any.callMethodIfExists(
    methodName: String, vararg args: Any
): Any? {
    var result: Any? = null
    runCatching { this.callMethod(methodName, *args) }.onSuccess { result = it }
    return result
}


/**
 * Invokes an instance method on this object only if it exists, using explicit parameter
 * types for disambiguation. Returns `null` if the method is not found or throws an exception.
 *
 * @param methodName the name of the instance method to invoke
 * @param parameterTypes the array of parameter types to match the exact method signature
 * @param args the arguments to pass to the method
 * @return the return value of the invoked method, or `null` if the method does not exist or fails
 */
fun Any.callMethodIfExists(
    methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any
): Any? {
    var result: Any? = null
    runCatching { this.callMethod(methodName, parameterTypes, *args) }.onSuccess { result = it }
    return result
}

/**
 * Invokes a static method on this [Class] only if it exists, returning `null` if the
 * method is not found or throws an exception. Unlike [callStaticMethod], this variant does
 * not propagate errors, making it safe for optional static method invocations.
 *
 * @param methodName the name of the static method to invoke
 * @param args the arguments to pass to the method
 * @return the return value of the invoked method, or `null` if the method does not exist or fails
 */
fun Class<*>.callStaticMethodIfExists(
    methodName: String, vararg args: Any
): Any? {
    var result: Any? = null
    runCatching { this.callStaticMethod(methodName, *args) }.onSuccess { result = it }
    return result
}

/**
 * Invokes a static method on this [Class] only if it exists, using explicit parameter types
 * for disambiguation. Returns `null` if the method is not found or throws an exception.
 *
 * @param methodName the name of the static method to invoke
 * @param parameterTypes the array of parameter types to match the exact method signature
 * @param args the arguments to pass to the method
 * @return the return value of the invoked method, or `null` if the method does not exist or fails
 */
fun Class<*>.callStaticMethodIfExists(
    methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any
): Any? {
    var result: Any? = null
    runCatching { this.callStaticMethod(methodName, parameterTypes, *args) }.onSuccess { result = it }
    return result
}

