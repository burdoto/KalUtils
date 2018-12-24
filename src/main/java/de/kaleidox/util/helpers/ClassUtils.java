package de.kaleidox.util.helpers;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class ClassUtils {
    /**
     * Extracts a field from a class or any of its superclasses; if possible, and casts the value to T.
     *
     * @param read      The class to read the field from.
     * @param fieldName The name of the field.
     * @param asType    The type of the field.
     * @param target    The target object. May be {@code null} if the field is static.
     * @param <T>       Type variable for the return.
     * @return The value of the field.
     * @throws NoSuchFieldError       If the field could not be found.
     * @throws IllegalAccessException If the field is private, protected or package-protected.
     */
    public static <T> T getFieldOfClass(Class read, String fieldName, Class<T> asType, @Nullable Object target)
            throws NoSuchFieldError, IllegalAccessException {
        Field[] declared = read.getDeclaredFields();
        int i = 0;
        while (i < declared.length && !declared[i].getName().equalsIgnoreCase(fieldName)) i++;
        if (i == declared.length && read != Object.class)
            return getFieldOfClass(read.getSuperclass(), fieldName, asType, target);
        else if (read == Object.class)
            throw new NoSuchFieldError(String.format("Field %s not found!", fieldName));
        return asType.cast(declared[i].get(target));
    }
}
