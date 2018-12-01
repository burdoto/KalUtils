package de.kaleidox.util.toolchains;

import java.util.function.Predicate;

public final class Predicates {
    public static final Predicate<Object> IS_LONG = aLong -> {
        if (aLong instanceof Long) {
            return true;
        } else {
            try {
                Long.parseLong(aLong.toString());
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
    };

    public static final Predicate<Boolean> IDENTITY = bool -> bool;
}
