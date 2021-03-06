package de.kaleidox.util.helpers;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class OptionalHelper extends NullHelper {
    public static <T> Optional<T> or(Optional<T> opt1, Supplier<Optional<T>> opt2prov) {
        if (opt1.isPresent()) return opt1;
        return opt2prov.get();
    }
}
