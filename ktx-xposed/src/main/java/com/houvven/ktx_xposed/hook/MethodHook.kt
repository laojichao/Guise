package com.houvven.ktx_xposed.hook

import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import kotlin.jvm.Throws

/**
 * The global [LoadPackageParam] instance provided by the Xposed framework during package loading.
 * Must be initialized via [setLpparam] before any hook or class-loading operation is invoked.
 */
lateinit var lppram: LoadPackageParam

/**
 * Initializes the global [lppram] variable with the given [LoadPackageParam].
 * This should be called once at the beginning of the Xposed module's [de.robv.android.xposed.IXposedHookLoadPackage.handleLoadPackage]
 * to make the [classLoader] and class-lookup utilities available throughout the hook package.
 *
 * @param lpparam the [LoadPackageParam] provided by the Xposed framework
 */
fun setLpparam(lpparam: LoadPackageParam) {
    lppram = lpparam
}

/**
 * The [ClassLoader] for the currently loaded application package.
 * Derived from [lppram] and used by class-lookup functions such as [findClass].
 *
 * @throws IllegalStateException if [lppram] has not been initialized via [setLpparam]
 */
val classLoader: ClassLoader
    get() = if (::lppram.isInitialized) lppram.classLoader else throw IllegalStateException("lpparam is not initialized")

/**
 * Loads a class by its fully qualified name using the current application [classLoader].
 *
 * @param className the fully qualified class name to load (e.g., `"com.example.Target"`)
 * @return the [Class] object for the specified class name
 * @throws XposedHelpers.ClassNotFoundError if the class cannot be found
 */
@Throws(XposedHelpers.ClassNotFoundError::class)
fun findClass(className: String): Class<*> = XposedHelpers.findClass(className, classLoader)

/**
 * Attempts to load a class by its fully qualified name, returning `null` if the class
 * is not found. This is a safe alternative to [findClass] that does not throw.
 *
 * @param className the fully qualified class name to load
 * @return the [Class] object, or `null` if the class does not exist
 */
fun findClassIfExists(className: String): Class<*>?  =
    XposedHelpers.findClassIfExists(className, classLoader)


/**
 * Hooks a method in the given class by name. The last vararg argument must be an
 * [XC_MethodHook] callback instance; preceding vararg entries are treated as parameter
 * types to match the target method signature.
 *
 * @param clazz the class containing the target method
 * @param methodName the name of the method to hook
 * @param parameterTypesAndCallback the parameter types followed by the [XC_MethodHook] callback
 * @return the [XC_MethodHook.Unhook] object, or `null` if an error occurs
 */
fun hookMethod(clazz: Class<*>, methodName: String, vararg parameterTypesAndCallback: Any) =
    runXposedCatching {
        XposedHelpers.findAndHookMethod(clazz, methodName, *parameterTypesAndCallback)
    }

/**
 * Hooks all overloaded methods with the given name in the specified class.
 * Uses [XposedBridge.hookAllMethods] which does not require parameter type matching.
 *
 * @param clazz the class containing the target methods
 * @param methodName the name of the methods to hook
 * @param callback the [XC_MethodHook] callback to execute when any overload is called
 * @return a [Set] of [XC_MethodHook.Unhook] objects for all hooked overloads
 */
fun hookAllMethods(clazz: Class<*>, methodName: String, callback: XC_MethodHook) =
    XposedBridge.hookAllMethods(clazz, methodName, callback)

/**
 * Hooks a constructor in the given class. The last vararg argument must be an
 * [XC_MethodHook] callback instance; preceding vararg entries are treated as parameter
 * types to match the target constructor signature.
 *
 * @param clazz the class containing the target constructor
 * @param parameterTypesAndCallback the parameter types followed by the [XC_MethodHook] callback
 * @return the [XC_MethodHook.Unhook] object, or `null` if an error occurs
 */
fun hookConstructor(clazz: Class<*>, vararg parameterTypesAndCallback: Any) =
    runXposedCatching {
        XposedHelpers.findAndHookConstructor(clazz, *parameterTypesAndCallback)
    }

/**
 * Hooks all constructors in the specified class, regardless of parameter signature.
 * Uses [XposedBridge.hookAllConstructors].
 *
 * @param clazz the class whose constructors should be hooked
 * @param callback the [XC_MethodHook] callback to execute when any constructor is called
 * @return a [Set] of [XC_MethodHook.Unhook] objects for all hooked constructors
 */
fun hookAllConstructors(clazz: Class<*>, callback: XC_MethodHook) =
    XposedBridge.hookAllConstructors(clazz, callback)

