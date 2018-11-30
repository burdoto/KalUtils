package de.kaleidox.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a varargs-type parameter to contain an amount of parameters that will return 0 on the modulo of
 * {@link #each()}.
 * If this is not the case, the method may throw an {@link IllegalArgumentException}.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
public @interface Grouping {
    int each() default 2;
}
