package de.kaleidox.util.helpers;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

public class CollectionHelper extends NullHelper {
    public static <T> Collection<T> requireNoNull(Collection<T> collection) {
        for (Object item : collection) {
            Objects.requireNonNull(item);
        }
        return collection;
    }

    static void nullChecks(Collection<?>... lists) {
        for (Collection<?> x : lists) {
            Objects.requireNonNull(x);
            requireNoNull(x);
        }
    }
}
