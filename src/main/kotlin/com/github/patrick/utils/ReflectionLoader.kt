package com.github.patrick.utils

import java.lang.reflect.Field
import java.lang.reflect.Modifier.FINAL

@Suppress("unused")
object ReflectionLoader {
    @JvmStatic
    fun getField(fieldClass: Any, fieldName: String): Field {
        val field = fieldClass::class.java.getDeclaredField(fieldName)
        val modifier = Field::class.java.getDeclaredField("modifiers")
        field.isAccessible = true
        modifier.isAccessible = true
        modifier.setInt(field, field.modifiers and FINAL.inv())
        return field
    }

    @JvmStatic
    fun getFieldValue(fieldClass: Any, fieldName: String): Any? = getField(fieldClass, fieldName).get(fieldClass)

    @JvmStatic
    fun setFieldValue(fieldClass: Any, fieldName: String, newValue: Any?): Any? = getField(fieldClass, fieldName).set(fieldClass, newValue)
}