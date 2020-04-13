package com.github.patrick.utils.bukkit;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes", "unused"})
public final class BukkitClassLoader {
    private BukkitClassLoader() {}

    @SuppressWarnings("unchecked")
    public static <T> T load(@NotNull final Class<T> type, @NotNull final String prefix, @NotNull final String version, @NotNull final Object... initialArgs) throws UnsupportedOperationException {
        String packageName = type.getPackage().getName();
        String className = prefix + type.getSimpleName();
        int lastDot = packageName.lastIndexOf('.');
        Class[] parameterTypes = ClassUtils.toClass(initialArgs);
        List<String> candidates = new ArrayList<>(2);
        candidates.add(packageName + '.' + version + '.' + className);

        if (lastDot > 0) {
            String superPackageName = packageName.substring(0, lastDot);
            String subPackageName = packageName.substring(lastDot + 1);
            candidates.add(superPackageName + '.' + version + '.' + subPackageName + '.' + className);
        }

        try {
            Class<? extends T> nmsClass = null;
            for (String name : candidates) {
                try { nmsClass = Class.forName(name, true, type.getClassLoader()).asSubclass(type); } catch (ClassNotFoundException ignored) {}
            }
            if (nmsClass == null) throw new ClassNotFoundException("Not found nms library class: " + candidates.toString());
            Constructor<?> constructor = ConstructorUtils.getMatchingAccessibleConstructor(nmsClass, parameterTypes);

            if (constructor == null)
                throw new UnsupportedOperationException(type.getName() + " does not have Constructor for [" + StringUtils.join(parameterTypes, ", ") + "]");
            return (T) constructor.newInstance(initialArgs);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(type.getName() + " does not support this version " + version, e);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(type.getName() + " constructor is not visible");
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(type.getName() + " is abstract class");
        } catch (InvocationTargetException e) {
            throw new UnsupportedOperationException(type.getName() + " has an error occurred while creating the instance", e);
        }
    }

    @NotNull
    public static String getBukkitVersion(@NotNull Object bukkitClass) {
        String packageName = bukkitClass.getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf(".") + 1);
    }
}