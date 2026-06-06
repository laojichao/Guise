@file:Suppress("unused")
package com.houvven.ktx_xposed.hook

import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.jvm.Throws

// ==================== Field Lookup ====================

/**
 * Finds a field in this [Class] by name using [XposedHelpers.findField].
 *
 * @param fieldName the name of the field to find
 * @return the [Field] object for the requested field
 * @throws NoSuchFieldError if the field does not exist in this class or its superclasses
 */
@Throws(NoSuchFieldError::class)
fun Class<*>.findField(fieldName: String): Field =
    XposedHelpers.findField(this, fieldName)

/**
 * Finds a field in this [Class] by name, returning `null` if the field is not found.
 * This is a safe alternative to [findField] that does not throw on missing fields.
 *
 * @param fieldName the name of the field to find
 * @return the [Field] object, or `null` if the field does not exist
 */
fun Class<*>.findFiledIfExists(fieldName: String): Field? =
    XposedHelpers.findFieldIfExists(this, fieldName)

/**
 * Finds the first field in this [Class] whose declared type matches the given [type] exactly.
 *
 * @param type the exact [Class] type to match against field declarations
 * @return the first matching [Field], or `null` if no field has the exact type
 * @throws NoSuchFieldError if no field with the given type exists
 */
@Throws(NoSuchFieldError::class)
fun Class<*>.findFirstFieldByExactType(type: Class<*>): Field? =
    XposedHelpers.findFirstFieldByExactType(this, type)

/* @Throws(NoSuchFieldError::class)
fun KClass<*>.findField(fieldName: String) = this.java.findField(fieldName)

fun KClass<*>.findFiledIfExists(fieldName: String) = this.java.findFiledIfExists(fieldName)

@Throws(NoSuchFieldError::class)
fun KClass<*>.findFirstFieldByExactType(type: Class<*>) = this.java.findFirstFieldByExactType(type) */

// ==================== Method Lookup ====================

/**
 * Finds an exact method in this [Class] by name and parameter types using
 * [XposedHelpers.findMethodExact].
 *
 * @param name the name of the method to find
 * @param parameterTypes the exact parameter types of the method (defaults to empty for no-arg)
 * @return the [Method] object for the requested method
 * @throws NoSuchMethodError if the method does not exist with the given signature
 * @throws XposedHelpers.ClassNotFoundError if a parameter type class cannot be resolved
 */
@Throws(NoSuchMethodError::class, XposedHelpers.ClassNotFoundError::class)
fun Class<*>.findMethodExact(name: String, vararg parameterTypes: Class<*> = arrayOf()): Method =
    XposedHelpers.findMethodExact(this, name, *parameterTypes)

/**
 * Finds an exact method in this [Class] by name and parameter types.
 * This overload accepts mixed types (both [Class] and [String] for class names),
 * which allows using string class names as shorthand for type resolution.
 *
 * @param name the name of the method to find
 * @param parameterTypes the parameter types, which can be [Class] instances or [String] class names
 * @return the [Method] object for the requested method
 * @throws NoSuchMethodError if the method does not exist with the given signature
 * @throws XposedHelpers.ClassNotFoundError if a parameter type class cannot be resolved
 */
@Throws(NoSuchMethodError::class, XposedHelpers.ClassNotFoundError::class)
fun Class<*>.findMethodExact(name: String, vararg parameterTypes: Any): Method =
    XposedHelpers.findMethodExact(this, name, *parameterTypes)

/**
 * Finds an exact method in this [Class] by name and parameter types, returning `null` if
 * the method is not found. This is a safe alternative to [findMethodExact] that does not
 * throw on missing methods.
 *
 * @param name the name of the method to find
 * @param parameterTypes the exact parameter types of the method (defaults to empty for no-arg)
 * @return the [Method] object, or `null` if the method does not exist
 */
fun Class<*>.findMethodExactIfExists(name: String, vararg parameterTypes: Class<*> = arrayOf()): Method? {
    var method: Method? = null
    kotlin
        .runCatching { this@findMethodExactIfExists.findMethodExact(name, *parameterTypes) }
        .onSuccess { method = it }

    return method
}

/**
 * Finds an exact method in this [Class] by name and parameter types (as mixed [Any] types),
 * returning `null` if the method is not found.
 *
 * @param name the name of the method to find
 * @param parameterTypes the parameter types, which can be [Class] instances or [String] class names
 * @return the [Method] object, or `null` if the method does not exist
 */
fun Class<*>.findMethodExactIfExists(name: String, vararg parameterTypes: Any): Method? =
    XposedHelpers.findMethodExactIfExists(this, name, *parameterTypes)

/* @Throws(NoSuchMethodError::class, XposedHelpers.ClassNotFoundError::class)
fun KClass<*>.findMethodExact(name: String, vararg parameterTypes: Class<*> = arrayOf()) =
    this.java.findMethodExact(name, *parameterTypes)

@Throws(NoSuchMethodError::class, XposedHelpers.ClassNotFoundError::class)
fun KClass<*>.findMethodExact(name: String, vararg parameterTypes: Any) =
    this.java.findMethodExact(name, *parameterTypes)

fun KClass<*>.findMethodExactIfExists(name: String, vararg parameterTypes: Class<*> = arrayOf()) =
    this.java.findMethodExactIfExists(name, *parameterTypes)

fun KClass<*>.findMethodExactIfExists(name: String, vararg parameterTypes: Any) =
    this.java.findMethodExactIfExists(name, *parameterTypes) */

/**
 * Finds the best-matching method in this [Class] by name and parameter types.
 * Unlike [findMethodExact], this method tolerates type widening and will find the closest
 * match when exact types are not available.
 *
 * @param name the name of the method to find
 * @param parameterTypes the desired parameter types (defaults to empty for no-arg)
 * @return the best-matching [Method] object
 * @throws NoSuchMethodError if no compatible method is found
 */
@Throws(NoSuchMethodError::class)
fun Class<*>.findMethodBestMatch(name: String, vararg parameterTypes: Class<*> = arrayOf()): Method =
    XposedHelpers.findMethodBestMatch(this, name, *parameterTypes)

/**
 * Finds the best-matching method in this [Class] by name using mixed type descriptors.
 * Accepts both [Class] and [String] class names for parameter type specification.
 *
 * @param name the name of the method to find
 * @param parameterTypes the desired parameter types as mixed [Any] values
 * @return the best-matching [Method] object
 * @throws NoSuchMethodError if no compatible method is found
 */
@Throws(NoSuchMethodError::class)
fun Class<*>.findMethodBestMatch(name: String, vararg parameterTypes: Any): Method =
    XposedHelpers.findMethodBestMatch(this, name, *parameterTypes)

/**
 * Finds the best-matching method in this [Class] by name using both explicit parameter types
 * and actual argument values for more accurate matching.
 *
 * @param name the name of the method to find
 * @param parameterTypes the declared parameter types
 * @param args the actual argument values to use for matching
 * @return the best-matching [Method] object, or `null` if no compatible method is found
 */
fun Class<*>.findMethodBestMatch(name: String, parameterTypes: Array<Class<*>>, args: Array<Any>): Method? =
    XposedHelpers.findMethodBestMatch(this, name, parameterTypes, args)


/**
 * Finds all methods in this [Class] that match the exact return type and parameter types.
 *
 * @param returnType the exact return type to match
 * @param parameterTypes the exact parameter types to match (defaults to empty for no-arg)
 * @return an array of matching [Method] objects (may be empty if no methods match)
 */
fun Class<*>.findMethodsByExactParameters(returnType: Class<*>, vararg parameterTypes: Class<*> = arrayOf()): Array<Method> =
    XposedHelpers.findMethodsByExactParameters(this, returnType, *parameterTypes)


