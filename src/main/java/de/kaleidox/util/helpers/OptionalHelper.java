package de.kaleidox.util.helpers;

import java.util.Optional;
import java.util.function.Supplier;

public class OptionalHelper extends NullHelper {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Optional<T> or(Optional<T> opt1, Supplier<Optional<T>> opt2prov) {
        if (opt1.isPresent()) return opt1;
        return opt2prov.get();
    }
}
