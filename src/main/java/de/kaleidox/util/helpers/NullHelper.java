package de.kaleidox.util.helpers;

import java.util.Objects;
import java.util.function.Supplier;

public class NullHelper {
    public static <T> T orDefault(T item, T def) {
        requireNonNull(item, def);
        return (item == null ? def : item);
    }

    public static void requireNonNull(Object... items) {
        for (Object item : items) {
            Objects.requireNonNull(item);
        }
    }

    public static <T> Object requireNonNullElse(T test, T orElse) {
        if (Objects.isNull(test)) return orElse;
        return test;
    }

    public static <O> O assure(O ptr, Supplier<O> supp) {
        //noinspection UnusedAssignment -> Because the pointer needs to be filled
        return ptr == null ? (ptr = supp.get()) : ptr;
    }
}
