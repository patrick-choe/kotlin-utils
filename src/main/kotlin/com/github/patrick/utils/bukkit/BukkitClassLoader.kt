package com.github.patrick.utils.bukkit

import org.apache.commons.lang3.ClassUtils.toClass
import org.apache.commons.lang3.reflect.ConstructorUtils.getMatchingAccessibleConstructor

import java.util.ArrayList

@Suppress("unused")
object BukkitClassLoader {
    @Throws(ClassNotFoundException::class, UnsupportedOperationException::class, UnsupportedClassVersionError::class, IllegalAccessError::class, InstantiationError::class)
    fun <T> load(classType: Class<T>, prefix: String, version: String, vararg initialArgs: Any?): T {
        val packageName = classType.`package`.name
        val className = "$prefix${classType.simpleName}"
        val candidates: MutableList<String> = ArrayList(2)
        val lastDot = packageName.lastIndexOf('.')
        var nmsClass: Class<out T>? = null
        candidates.add("$packageName.$version.$className")
        if (lastDot > 0) candidates.add("${packageName.substring(0, lastDot)}.$version.${packageName.substring(lastDot + 1)}.$className")
        return try {
            candidates.forEach {
                try {
                    nmsClass = nmsClass?: Class.forName(it, true, classType.classLoader).asSubclass(classType)
                } catch (ignored: ClassNotFoundException) {}
            }
            (getMatchingAccessibleConstructor(
                (nmsClass?: throw ClassNotFoundException("Not found class: $candidates")),
                *toClass(initialArgs))?: throw UnsupportedOperationException("${classType.name} does not have constructor for [${toClass(initialArgs).joinToString()}]"
            )).newInstance(initialArgs)
        } catch (e: ClassNotFoundException) { throw UnsupportedClassVersionError("${classType.name} does not support this version '$version'")
        } catch (e: IllegalAccessException) { throw IllegalAccessError("${classType.name} constructor is not visible")
        } catch (e: InstantiationException) { throw InstantiationError("${classType.name} is abstract class or an interface") }
    }
}