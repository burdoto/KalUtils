package de.kaleidox.util.helpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class UrlHelper extends NullHelper {
    /**
     * Creates an URL from the given String. Returns {@code null} if the given String is null. Returns {@code null} and logs a {@link MalformedURLException} if
     * the URL is malformed.
     *
     * @param of The string to create an URL from.
     * @return An URL or {@code null}.
     */
    public static URL ignoreIfNull(String of) {
        if (Objects.nonNull(of)) {
            try {
                return new URL(of);
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return null;
    }

    public static URL orNull(String of) {
        if (Objects.nonNull(of)) {
            try {
                return new URL(of);
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return null;
    }

    public static boolean equals(@Nullable Object first, @Nullable Object second) {
        if (first == null || second == null) return false;
        if (first.getClass() == second.getClass()) return first.equals(second);
        if (first instanceof String && second instanceof URL) return ((URL) second).toExternalForm()
                .equalsIgnoreCase((String) first);
        else if (first instanceof URL && second instanceof String) return ((URL) first).toExternalForm()
                .equalsIgnoreCase((String) second);
        return false;
    }

    /**
     * Creates an URL from the given String that must not be null. Returns {@code null} and logs a {@link MalformedURLException} if the URL is malformed.
     *
     * @param of The string to create an URL from.
     * @return An URL or {@code null}.
     */
    public static URL require(String of) {
        Objects.requireNonNull(of);
        try {
            return new URL(of);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("MalformedURLException: " + e.getMessage());
        }
    }
}
