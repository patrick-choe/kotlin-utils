package com.github.patrick.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings("unused")
public class ReflectionLoader {
    private ReflectionLoader() {}

    @NotNull
    public static Field getField(@NotNull Object fieldObject, @NotNull String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = fieldObject.getClass().getField(fieldName);
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        return field;
    }

    @Nullable
    public static Object getFieldValue(@NotNull Object fieldObject, @NotNull String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getField(fieldObject, fieldName).get(fieldObject);
    }

    public static void setFieldValue(@NotNull Object fieldObject, @NotNull String fieldName, @Nullable Object newValue) throws NoSuchFieldException, IllegalAccessException {
        getField(fieldObject, fieldName).set(fieldObject, newValue);
    }
}
