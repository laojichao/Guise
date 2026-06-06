package com.houvven.ktx_xposed.hook

import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.XposedHelpers

/**
 * Sets a static field on the class identified by [className].
 * The class is resolved via [findClass] using the current [classLoader].
 *
 * @param T the type of the value to set; used for automatic type dispatch
 * @param className the fully qualified name of the class containing the static field
 * @param fieldName the name of the static field to modify
 * @param value the new value to assign to the field
 */
inline fun <reified T> setStaticField(className: String, fieldName: String, value: T) {
    runXposedCatching {
        findClass(className).setStaticField(fieldName, value)
    }
}

/**
 * Sets a static field on this [Class] using type-aware dispatch.
 * The value type is matched at runtime against primitive types (Boolean, Byte, Char, Short,
 * Int, Long, Float, Double) and dispatched to the corresponding [XposedHelpers] setter.
 * Non-primitive types fall through to [XposedHelpers.setStaticObjectField].
 *
 * @param T the type of the value to set; used for automatic type dispatch
 * @param fieldName the name of the static field to modify
 * @param value the new value to assign to the field
 */
inline fun <reified T> Class<*>.setStaticField(fieldName: String, value: T) {
    runXposedCatching {
        when (value) {
            is Boolean -> XposedHelpers.setStaticBooleanField(this, fieldName, value)
            is Byte -> XposedHelpers.setStaticByteField(this, fieldName, value)
            is Char -> XposedHelpers.setStaticCharField(this, fieldName, value)
            is Short -> XposedHelpers.setStaticShortField(this, fieldName, value)
            is Int -> XposedHelpers.setStaticIntField(this, fieldName, value)
            is Long -> XposedHelpers.setStaticLongField(this, fieldName, value)
            is Float -> XposedHelpers.setStaticFloatField(this, fieldName, value)
            is Double -> XposedHelpers.setStaticDoubleField(this, fieldName, value)
            else -> XposedHelpers.setStaticObjectField(this, fieldName, value)
        }
    }
}

/**
 * Sets an instance field on the given [instance] object using type-aware dispatch.
 * The value type is matched at runtime against primitive types (Boolean, Byte, Char, Short,
 * Int, Long, Float, Double) and dispatched to the corresponding [XposedHelpers] setter.
 * Non-primitive types fall through to [XposedHelpers.setObjectField].
 *
 * @param T the type of the value to set; used for automatic type dispatch
 * @param instance the object instance whose field should be modified
 * @param fieldName the name of the instance field to modify
 * @param value the new value to assign to the field
 */
inline fun <reified T> setInstanceField(instance: Any, fieldName: String, value: T) {
    runXposedCatching {
        when (value) {
            is Boolean -> XposedHelpers.setBooleanField(instance, fieldName, value)
            is Byte -> XposedHelpers.setByteField(instance, fieldName, value)
            is Char -> XposedHelpers.setCharField(instance, fieldName, value)
            is Short -> XposedHelpers.setShortField(instance, fieldName, value)
            is Int -> XposedHelpers.setIntField(instance, fieldName, value)
            is Long -> XposedHelpers.setLongField(instance, fieldName, value)
            is Float -> XposedHelpers.setFloatField(instance, fieldName, value)
            is Double -> XposedHelpers.setDoubleField(instance, fieldName, value)
            else -> XposedHelpers.setObjectField(instance, fieldName, value)
        }
    }
}
